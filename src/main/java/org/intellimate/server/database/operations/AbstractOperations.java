package org.intellimate.server.database.operations;

import org.jooq.*;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.intellimate.server.database.model.Tables.*;

/**
 * base class for all operations
 * @author LeanderK
 * @version 1.0
 */
public abstract class AbstractOperations {
    protected final DSLContext create;

    protected AbstractOperations(DSLContext create) {
        this.create = create;
    }

    /**
     * this method returns a range of results from a passed query.
     * @param query the query to use
     * @param primaryKey the primary key used to index the records inside the range
     * @param start the exclusive start, when the associated record does not fulfill the conditions of the passed query
     *              or is not existing, the range-object communicates that there are no elements left (next = true) or
     *              right (next=false) of the range.
     * @param next whether the Range is right (true) or left of the primary key (false) assuming natural order
     * @param limit the max. amount of the range, may be smaller
     * @param <R> the type of the records
     * @return an instance of Range
     * @see #getNextRange(SelectWhereStep, Field, Table, Object, boolean, int, Comparator)
     */
    protected <R extends org.jooq.Record> Range<R, Integer> getNextRange(SelectWhereStep<R> query, Field<Integer> primaryKey, Table<?> tablePrimaryKey,
                                                                         Integer start, boolean next, int limit) {
        return getNextRange(query, primaryKey, tablePrimaryKey, start, next, limit, Comparator.naturalOrder());
    }

    /**
     * this method returns a range of results from a passed query.
     * @param query the query to use
     * @param primaryKey the primary key used to index the records inside the range
     * @param start the exclusive start, when the associated record does not fulfill the conditions of the passed query
     *              or is not existing, the range-object communicates that there are no elements left (next = true) or
     *              right (next=false) of the range.
     * @param next whether the Range is right (true) or left of the primary key (false) assuming natural order
     * @param limit the max. amount of the range, may be smaller
     * @param <R> the type of the records
     * @return an instance of Range
     * @see #getNextRange(SelectWhereStep, Field, Table, Object, boolean, int, Comparator)
     */
    protected <R extends org.jooq.Record> Range<R, Integer> getNextRange(SelectConditionStep<R> query, Field<Integer> primaryKey, Table<?> tablePrimaryKey,
                                                                         Integer start, boolean next, int limit) {
        return getNextRange(query, primaryKey, tablePrimaryKey, start, next, limit, Comparator.naturalOrder());
    }

    /**
     * this method returns a range of results from a passed query.
     * @param query the query to use
     * @param primaryKey the primary key used to index the records inside the range
     * @param start the exclusive start, when the associated record does not fulfill the conditions of the passed query
     *              or is not existing, the range-object communicates that there are no elements left (next = true) or
     *              right (next=false) of the range.
     * @param next whether the Range is right (true) or left of the primary key (false) assuming natural order
     * @param limit the max. amount of the range, may be smaller
     * @param sort the comparator used to sort the elements
     * @param <R> the type of the records
     * @param <K> the type of the primary-key
     * @return an instance of Range
     * @see #getNextRange(SelectWhereStep, Field, Table, Object, boolean, int, Comparator)
     */
    protected <R extends org.jooq.Record, K> Range<R, K> getNextRange(SelectWhereStep<R> query, Field<K> primaryKey, Table<?> tablePrimaryKey,
                                                                      K start, boolean next, int limit, Comparator<K> sort) {
        return getNextRange(query.where(true), primaryKey, tablePrimaryKey, start, next, limit, sort);
    }

    /**
     * this method returns a range of results from a passed query.
     * The range is indexed by a primary key to support good performance for bigger datasets. The resulting range always
     * starts after the passed start and tries to get the amount of records specified with limit. The next-modifier decides
     * whether the range should be after or before the start-parameter.
     * An application for this method could be paging where for a specific query you only want a specific range of results.
     * @param query the query to use
     * @param primaryKey the primary key used to index the records inside the range
     * @param start the exclusive start, when the associated record does not fulfill the conditions of the passed query
     *              or is not existing, the range-object communicates that there are no elements left (next = true) or
     *              right (next=false) of the range.
     * @param next whether the Range is right (true) or left of the primary key (false) assuming natural order
     * @param limit the max. amount of the range, may be smaller
     * @param sort the comparator used to sort the elements
     * @param <R> the type of the records
     * @param <K> the type of the primary-key
     * @return an instance of Range
     */
    protected <R extends org.jooq.Record, K> Range<R, K> getNextRange(SelectConditionStep<R> query, Field<K> primaryKey, Table<?> tablePrimaryKey,
                                                                      K start, boolean next, int limit, Comparator<K> sort) {
        //right now no support for joins with a 1 to n relationship
        Condition primaryKeyCondition = next
                ? primaryKey.greaterOrEqual(start)
                : primaryKey.lessOrEqual(start);

        SortField<K> sortField = next
                ? primaryKey.asc()
                : primaryKey.desc();

        Result<R> results = query.and(primaryKeyCondition)
                .orderBy(sortField)
                .limit(limit + 2)
                .fetch();

        int toSkip = 0;

        if (results.isNotEmpty() && results.get(0).getValue(primaryKey).equals(start)) {
            toSkip = 1;
        }

        List<R> sortedResults = results.stream()
                .skip(toSkip)
                .limit(limit)
                .sorted(Comparator.comparing(record -> record.getValue(primaryKey), sort))
                .collect(Collectors.toList());

        K left = null;
        K right = null;

        if (!sortedResults.isEmpty()) {
            left = sortedResults.get(0).getValue(primaryKey);
            right = sortedResults.get(sortedResults.size() - 1).getValue(primaryKey);
        }

        boolean hasPredecessors = toSkip == 1;
        boolean hasSuccessors = results.size() > limit + toSkip;

        if (toSkip == 0) {
            primaryKeyCondition = next
                    ? primaryKey.lessThan(start)
                    : primaryKey.greaterThan(start);

            // FIXME: Ugly hack, otherwise we end up with id >= ? and id < ?
            Optional<R> before = query.and(false).or(primaryKeyCondition).limit(1).fetchOptional();
            hasPredecessors = before.isPresent();
        }

        if (next) {
            return Range.of(sortedResults, left, right, hasPredecessors, hasSuccessors);
        } else {
            return Range.of(sortedResults, left, right, hasSuccessors, hasPredecessors);
        }
    }
}
