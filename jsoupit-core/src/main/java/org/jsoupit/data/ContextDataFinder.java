package org.jsoupit.data;

import org.jsoupit.Context;

public interface ContextDataFinder {
    public Object findDataInContext(Context context, String scope, String name, Class<?> type);
}
