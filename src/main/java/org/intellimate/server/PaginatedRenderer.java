package org.intellimate.server;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import org.intellimate.server.rest.Paginated;
import ratpack.handling.Context;
import ratpack.http.internal.MimeParse;
import ratpack.render.RendererSupport;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author LeanderK
 * @version 1.0
 */
public class PaginatedRenderer extends RendererSupport<Paginated<?>> {
    private static JsonFormat.Printer PRINTER = JsonFormat.printer().includingDefaultValueFields();

    private final static String TYPE_JSON = "application/json";
    private final static String TYPE_PROTOBUF = "application/protobuf";
    private final static List<String> SUPPORTED_TYPES = Collections.unmodifiableList(Arrays.asList(
            "application/protobuf",
            "application/json"
    ));

    /**
     * {@inheritDoc}
     *
     * @param context
     * @param paginated
     */
    @Override
    public void render(Context context, Paginated<?> paginated) throws Exception {
        String bestMatch = MimeParse.bestMatch(SUPPORTED_TYPES, context.getRequest().getHeaders().get("accept"));

        StringBuilder linkBuilder = new StringBuilder();

        if (paginated.hasPrevious()) {
            Map<String, String> params = new HashMap<>();
            paginated.getLeft().ifPresent(x -> params.put("from", x.toString()));
            params.put("asc", "false");

            linkBuilder.append("<").append(link(context, params)).append(">; rel=\"prev\", ");
        }

        if (paginated.hasNext()) {
            Map<String, String> params = new HashMap<>();
            paginated.getRight().ifPresent(x -> params.put("from", x.toString()));
            params.put("asc", "true");

            linkBuilder.append("<").append(link(context, params)).append(">; rel=\"next\", ");
        }

        String link = linkBuilder.toString();

        if (!link.isEmpty()) {
            link = link.substring(0, link.length() - 2); // remove trailing comma
            context.getResponse().getHeaders().add("Link", link);
        }

        Message message = paginated.getMessage();

        try {
            switch (bestMatch) {
                case TYPE_JSON:
                    context.getResponse().send(TYPE_JSON, PRINTER.print(message));
                    context.getResponse().contentType(TYPE_JSON);
                    break;
                case TYPE_PROTOBUF:
                    context.getResponse().send(TYPE_PROTOBUF, message.toByteArray());
                    context.getResponse().contentType(TYPE_PROTOBUF);
                    break;
                default:
                    throw new NotAcceptableException(context.getRequest().getHeaders().get("accept"), TYPE_JSON, TYPE_PROTOBUF);
            }
        } catch (InvalidProtocolBufferException e) {
            // Can't happen, because we don't use any "Any" fields.
            // https://developers.google.com/protocol-buffers/docs/proto3#any
            throw new InternalServerErrorException("Attempt to transform an invalid protocol buffer into JSON.");
        }
    }

    public static String link(Context context, Map<String, String> replaceQueryParams) {
        StringBuilder builder = new StringBuilder();
        URL url;

        try {
            url = new URL(context.getRequest().getUri());
        } catch (MalformedURLException e) {
            throw new InternalServerErrorException("Request URI could not be parsed!");
        }

        builder.append(url.getPath()).append("?");

        try {
            for (String key : context.getRequest().getQueryParams().getAll().keySet()) {
                if (replaceQueryParams.containsKey(key)) {
                    continue;
                }

                builder.append(URLEncoder.encode(key, "utf-8"));
                builder.append("=");
                builder.append(URLEncoder.encode(context.getRequest().getQueryParams().get(key), "utf-8"));
                builder.append("&");
            }

            for (String key : replaceQueryParams.keySet()) {
                builder.append(URLEncoder.encode(key, "utf-8"));
                builder.append("=");
                builder.append(URLEncoder.encode(replaceQueryParams.get(key), "utf-8"));
                builder.append("&");
            }
        } catch (UnsupportedEncodingException e) {
            throw new InternalServerErrorException("utf-8 is an unsupported encoding");
        }

        String link = builder.toString();

        if (link.isEmpty()) {
            return link;
        }

        return link.substring(0, link.length() - 1);
    }
}
