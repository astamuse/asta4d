package com.astamuse.asta4d.web.dispatch.response.provider;

import com.astamuse.asta4d.Page;
import com.astamuse.asta4d.web.dispatch.response.writer.Asta4DPageWriter;
import com.astamuse.asta4d.web.dispatch.response.writer.ContentWriter;

public class Asta4DPageProvider implements ContentProvider<Page> {

    public final static String AttrBodyOnly = Asta4DPageProvider.class.getName() + "##bodyOnly";

    private String path;

    public Asta4DPageProvider() {
        this("");
    }

    public Asta4DPageProvider(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean isContinuable() {
        return false;
    }

    @Override
    public Page produce() throws Exception {
        return new Page(path);
    }

    @Override
    public Class<? extends ContentWriter<Page>> getContentWriter() {
        return Asta4DPageWriter.class;
    }

}
