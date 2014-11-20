package com.astamuse.asta4d.web.form.field;

import com.astamuse.asta4d.Context;

public class PrepareRenderingDataUtil {

    private static final String dataStoreKey(String selector) {
        return selector + "#" + PrepareRenderingDataUtil.class.getName();
    }

    public static final void storeDataToContextBySelector(String editTargetSelector, String displayTargetSelector, Object data) {
        Context context = Context.getCurrentThreadContext();

        String storeKey = dataStoreKey(editTargetSelector);
        context.setData(storeKey, data);

        storeKey = dataStoreKey(displayTargetSelector);
        context.setData(storeKey, data);
    }

    public static <T> T retrieveStoredDataFromContextBySelector(String selector) {
        String storeKey = dataStoreKey(selector);
        Context context = Context.getCurrentThreadContext();
        return context.getData(storeKey);
    }
}
