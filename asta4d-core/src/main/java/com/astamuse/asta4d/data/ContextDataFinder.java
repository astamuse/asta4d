package com.astamuse.asta4d.data;

import com.astamuse.asta4d.Context;

/**
 * This interface declares how to find a certain data in Context. Because there
 * are customized scopes, so an implementation can customize the search logic
 * against special scope.
 * 
 * @author e-ryu
 * 
 */
public interface ContextDataFinder {

    /**
     * 
     * 
     * @param context
     * @param scope
     * @param name
     * @param type
     * @return
     * @throws DataOperationException
     */
    public Object findDataInContext(Context context, String scope, String name, Class<?> type) throws DataOperationException;
}
