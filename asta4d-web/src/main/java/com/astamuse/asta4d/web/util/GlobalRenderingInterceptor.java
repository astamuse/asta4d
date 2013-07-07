package com.astamuse.asta4d.web.util;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.astamuse.asta4d.interceptor.PageInterceptor;
import com.astamuse.asta4d.render.Renderer;

public class GlobalRenderingInterceptor implements PageInterceptor {

    @Override
    public void prePageRendering(Renderer renderer) {
        // do nothing
    }

    @Override
    public void postPageRendering(Renderer renderer) {
        Map<String, List<Renderer>> map = GlobalRenderingHelper.getSavedMap();
        if (map == null) {
            return;
        }

        Set<Entry<String, List<Renderer>>> set = map.entrySet();
        for (Entry<String, List<Renderer>> item : set) {
            renderer.add(item.getKey(), item.getValue());
        }
    }

}
