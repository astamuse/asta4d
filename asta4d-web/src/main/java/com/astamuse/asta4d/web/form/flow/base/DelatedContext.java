package com.astamuse.asta4d.web.form.flow.base;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.data.ContextDataHolder;
import com.astamuse.asta4d.web.WebApplicationContext;

public class DelatedContext extends Context {
    private Context context;

    private String arraySeq;

    public DelatedContext(Context context, int arraySeq) {
        this.context = context;
        this.arraySeq = String.valueOf(arraySeq);
    }

    public int hashCode() {
        return context.hashCode();
    }

    public void setCurrentRenderingElement(Element elem) {
        context.setCurrentRenderingElement(elem);
    }

    public Element getCurrentRenderingElement() {
        return context.getCurrentRenderingElement();
    }

    public void setData(String key, Object data) {
        context.setData(key, data);
    }

    public void setData(String scope, String key, Object data) {
        context.setData(scope, key, data);
    }

    private String convertArrayKey(String scope, String key) {
        if (scope.equals(WebApplicationContext.SCOPE_QUERYPARAM)) {
            return StringUtils.replace(key, "@", arraySeq);
        } else {
            return key;
        }
    }

    public <T> T getData(String key) {
        return context.getData(key);
    }

    public <T> T getData(String scope, String key) {
        return context.getData(scope, convertArrayKey(scope, key));
    }

    public <T> ContextDataHolder<T> getDataHolder(String key) {
        return context.getDataHolder(key);
    }

    public <T> ContextDataHolder<T> getDataHolder(String scope, String key) {
        return context.getDataHolder(scope, convertArrayKey(scope, key));
    }

    public boolean equals(Object obj) {
        return context.equals(obj);
    }

    public Locale getCurrentLocale() {
        return context.getCurrentLocale();
    }

    public void setCurrentLocale(Locale locale) {
        context.setCurrentLocale(locale);
    }

    public void init() {
        context.init();
    }

    public void clear() {
        context.clear();
    }

    public Context clone() {
        return context.clone();
    }

    public String toString() {
        return context.toString();
    }

}
