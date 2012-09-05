package org.jsoupit.template.data;

import org.jsoupit.template.Context;

public interface ContextDataFinder {
    public Object findDataInContext(Context context, String scope, String name, Class<?> type);
}
