package com.astamuse.asta4d.web.dispatch.request;

import java.util.List;

import com.astamuse.asta4d.web.dispatch.response.provider.ContentProvider;

public class ResultTransformerUtil {

    public final static ContentProvider<?> transform(Object result, List<ResultTransformer> transformerList) {

        if (result instanceof ContentProvider) {
            return (ContentProvider<?>) result;
        }

        ContentProvider<?> cp = null;
        Object before, after;
        before = result;
        for (ResultTransformer resultTransformer : transformerList) {
            after = resultTransformer.transformToContentProvider(before);
            if (after == null) {
                continue;
            } else if (after instanceof ContentProvider) {
                cp = (ContentProvider<?>) after;
                break;
            } else {
                before = after;
                continue;
            }
        }

        if (cp == null) {
            String msg = "Cannot recognize the result type of:%s. Maybe a ResultTransformer is neccessory.";
            String.format(msg, result.getClass().getName());
            throw new UnsupportedOperationException(msg);
        } else {

            return cp;
        }
    }
}
