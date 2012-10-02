package com.astamuse.asta4d.web.util;

import static com.astamuse.asta4d.web.WebApplicationContext.SCOPE_FLASH;

import java.util.Map;
import java.util.Map.Entry;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.util.IdGenerator;

public class RedirectUtil {
    public static final String KEY_FLASH_SCOPE_ID = "flash_scope_id";

    public static String setFlashScopeData(String url, Map<String, Object> flashScopeData) {
        if (flashScopeData.isEmpty()) {
            return url;
        }
        String flashScopeId = IdGenerator.createId();
        FlashScopeDataManager.getInstance().put(flashScopeId, flashScopeData);
        if (url.contains("?")) {
            return url + '&' + KEY_FLASH_SCOPE_ID + '=' + flashScopeId;
        }
        return url + '?' + KEY_FLASH_SCOPE_ID + '=' + flashScopeId;
    }

    public static void getFlashScopeData(String flashScopeId) {
        Context context = Context.getCurrentThreadContext();
        Map<String, Object> flashScopeData = FlashScopeDataManager.getInstance().get(flashScopeId);
        for (Entry<String, Object> entry : flashScopeData.entrySet()) {
            context.setData(SCOPE_FLASH, entry.getKey(), entry.getValue());
        }
    }

}
