package com.astamuse.asta4d.web.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.render.Renderer;

public class GlobalRenderingHelper {

    // private final static Map<String, List<Renderer>> msgRenderer

    private final static String GlobalRenderingKey = GlobalRenderingHelper.class.getName() + "#GlobalRenderingKey";

    public final static String DefaultGlobalContainerSelector = "#global-render-target";

    public final static void addRenderer(String containerSelector, Renderer renderer) {
        Map<String, List<Renderer>> map = getMap(true);
        List<Renderer> list = map.get(containerSelector);
        if (list == null) {
            list = new LinkedList<>();
            map.put(containerSelector, list);
        }
        list.add(renderer);
    }

    private final static Map<String, List<Renderer>> getMap(boolean create) {
        Context context = Context.getCurrentThreadContext();
        Map<String, List<Renderer>> map = context.getData(GlobalRenderingKey);
        if (map == null && create) {
            map = new HashMap<>();
            context.setData(GlobalRenderingKey, map);
        }
        return map;
    }

    public final static Map<String, List<Renderer>> getSavedMap() {
        return getMap(false);
    }
}
