package org.intellimate.server;

import org.intellimate.server.rest.Paginated;
import org.jooq.lambda.function.Function3;
import ratpack.handling.Context;
import ratpack.handling.Handler;

import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

import static com.sun.tools.doclint.Entity.or;

/**
 * this interface contains various convenience-methods to help to deal with SparkJava-Requests.
 * @author LeanderK
 * @version 1.0
 */
public interface RequestHelper {

    /**
     * if the predicate ist false, this method will throw an BadRequestException with the provided message as a cause.
     * @param context the context to check
     * @param predicate the predicate to match against
     * @param message the message describing the tested predicate
     * @throws BadRequestException if the predicate returned false
     */
    default void assertRequest(Context context, Predicate<Context> predicate, String message) throws BadRequestException {
        if (!predicate.test(context)) {
            throw new BadRequestException(message);
        }
    }

    /**
     * asserts that the content-type of the request is 'application/json'.
     * @param context the context to check
     * @throws BadRequestException if the request has the incorrect content-type
     */
    default void assertJson(Context context) throws BadRequestException {
        String actualContentType = context.getRequest().getContentType().getType();
        assertRequest(context, request1 -> actualContentType != null && actualContentType.equals("application/json"),
                "Content-Type must be 'application/json'!");
    }

    /**
     * asserts that the parameter exists and returns it
     * @param context the context to check
     * @param parameter the parameter to get
     * @return the value of the parameter
     * @throws BadRequestException if the parameter is not existing
     */
    default String assertParameter(Context context, String parameter) throws BadRequestException {
        assertRequest(context, context1 -> context1.getPathTokens().get(parameter) != null, "Request needs Parameter:" + parameter);
        return context.getPathTokens().get(parameter);
    }

    /**
     * asserts that the parameter exists and is an integer
     * @param context the context to check
     * @param parameter the parameter to get
     * @return the value as an integer
     * @throws BadRequestException if the parameter is not existing or is not an int
     */
    default int assertParameterInt(Context context, String parameter) throws BadRequestException {
        assertRequest(context, context1 -> context1.getPathTokens().get(parameter) != null, "Request needs Parameter:" + parameter);
        try {
            return Integer.parseInt(context.getPathTokens().get(parameter));
        } catch (NumberFormatException e) {
            throw new BadRequestException("Request needs Parameter: " + parameter + " as an Integer");
        }
    }

    /**
     * checks that an query-parameter exists and returns it or the default
     * @param context the context to check
     * @param parameter the query-parameter
     * @param defaultValue the defaultValue
     * @return the value of the parameter
     */
    default String queryOrDefault(Context context, String parameter, String defaultValue) throws BadRequestException {
        String param = context.getRequest().getQueryParams().get(parameter);
        if (param != null) {
            return param;
        } else {
            return defaultValue;
        }
    }

    /**
     * checks that an query-parameter exists and returns it or the default
     * @param context the context to check
     * @param parameter the query-parameter
     * @param defaultValue the defaultValue
     * @return the value of the parameter
     */
    default int queryOrDefault(Context context, String parameter, int defaultValue) throws BadRequestException {
        String param = context.getRequest().getQueryParams().get(parameter);
        if (param != null) {
            try {
                return Integer.parseInt(param);
            } catch (NumberFormatException e) {
                throw new BadRequestException("Request needs Query-Parameter: " + parameter + " as an Integer");
            }
        } else {
            return defaultValue;
        }
    }

    /**
     * checks that an query-parameter exists and returns it or the default
     * @param context the context to check
     * @param parameter the query-parameter
     * @param defaultValue the defaultValue
     * @return the value of the parameter
     */
    default boolean queryOrDefault(Context context, String parameter, boolean defaultValue) throws BadRequestException {
        String param = context.getRequest().getQueryParams().get(parameter);
        if (param != null) {
            if (param.equals("true")) {
                return true;
            } else if (param.equals("false")) {
                return false;
            } else {
                throw new BadRequestException("Request needs Query-Parameter: " + parameter + " as an Boolean");
            }
        } else {
            return defaultValue;
        }
    }

    /**
     * checks that an query-parameter exists and returns it
     * @param context the context to check
     * @param parameter the query-parameter
     * @return the value of the parameter
     * @throws BadRequestException if the query-parameter is not present
     */
    default String assertQuery(Context context, String parameter) throws BadRequestException {
        assertRequest(context, context1 -> context1.getRequest().getQueryParams().containsKey(parameter), "Request needs Query-Parameter:" + parameter);
        return context.getRequest().getQueryParams().get(parameter);
    }

    /**
     * checks that an query-parameter exists and is an integer
     * @param context the context to check
     * @param parameter the query-parameter
     * @return the value of the parameter as an int
     * @throws BadRequestException if the query parameter is not present or is not an int
     */
    default int assertQueryInt(Context context, String parameter) throws BadRequestException {
        assertRequest(context, context1 -> context1.getRequest().getQueryParams().containsKey(parameter), "Request needs Query-Parameter:" + parameter);
        try {
            return Integer.parseInt(context.getRequest().getQueryParams().get(parameter));
        } catch (NumberFormatException e) {
            throw new BadRequestException("Request needs Query-Parameter: " + parameter + " as an Integer");
        }
    }

    /**
     * resolve a integer-based paginated request
     * @param function the function to apply
     * @param <T> the index-type
     */
    default <T> Handler doPaginated(Function3<Integer, Boolean, Context, Paginated<T>> function) {
        return ctx -> {
            int from = queryOrDefault(ctx, "from", 0);
            boolean asc = queryOrDefault(ctx, "asc", true);
            ctx.render(function.apply(from, asc, ctx));
        };
    }

    /**
     * resolve a integer-based paginated request
     * @param function the function to apply
     * @param <T> the index-type
     */
    default <T> Handler doPaginated(BiFunction<Integer, Boolean, Paginated<T>> function) {
        return ctx -> {
            int from = queryOrDefault(ctx, "from", 0);
            boolean asc = queryOrDefault(ctx, "asc", true);
            ctx.render(function.apply(from, asc));
        };
    }
}
