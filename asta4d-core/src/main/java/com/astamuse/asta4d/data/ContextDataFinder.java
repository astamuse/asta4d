package com.astamuse.asta4d.data;

import com.astamuse.asta4d.Context;

public interface ContextDataFinder {
    public Object findDataInContext(Context context, String scope, String name, Class<?> type) throws DataOperationException;
}
