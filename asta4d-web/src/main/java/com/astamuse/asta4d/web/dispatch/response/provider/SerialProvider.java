package com.astamuse.asta4d.web.dispatch.response.provider;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.astamuse.asta4d.web.dispatch.response.writer.ContentWriter;
import com.astamuse.asta4d.web.dispatch.response.writer.SerialWriter;
import com.astamuse.asta4d.web.util.DeclareInstanceUtil;

@SuppressWarnings("rawtypes")
public class SerialProvider implements ContentProvider<List<Pair<Object, ContentWriter>>> {

    private List<ContentProvider<?>> contentProviderList = new ArrayList<>();

    public SerialProvider() {
    }

    public SerialProvider(ContentProvider<?>... contentProviders) {
        for (ContentProvider<?> contentProvider : contentProviders) {
            contentProviderList.add(contentProvider);
        }
    }

    public List<ContentProvider<?>> getContentProviderList() {
        return contentProviderList;
    }

    @Override
    public boolean isContinuable() {
        for (ContentProvider<?> cp : contentProviderList) {
            if (!cp.isContinuable()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<Pair<Object, ContentWriter>> produce() throws Exception {
        List<Pair<Object, ContentWriter>> list = new ArrayList<>(contentProviderList.size());
        Pair<Object, ContentWriter> p;
        for (ContentProvider<?> cp : contentProviderList) {
            p = Pair.of((Object) cp.produce(), (ContentWriter) DeclareInstanceUtil.createInstance(cp.getContentWriter()));
            list.add(p);
        }
        return list;

    }

    @Override
    public Class<? extends ContentWriter<List<Pair<Object, ContentWriter>>>> getContentWriter() {
        return SerialWriter.class;
    }

}
