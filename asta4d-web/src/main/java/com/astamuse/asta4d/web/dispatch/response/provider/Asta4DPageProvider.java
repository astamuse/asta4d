package com.astamuse.asta4d.web.dispatch.response.provider;

import com.astamuse.asta4d.web.dispatch.response.writer.Asta4DPageWriter;
import com.astamuse.asta4d.web.dispatch.response.writer.ContentWriter;

public class Asta4DPageProvider implements ContentProvider<Asta4DPage> {

    public final static String AttrBodyOnly = Asta4DPageProvider.class.getName() + "##bodyOnly";

    private String path;

    private boolean bodyOnly;

    public Asta4DPageProvider() {
        this("", false);
    }

    public Asta4DPageProvider(String path) {
        this(path, false);
    }

    public Asta4DPageProvider(String path, boolean bodyOnly) {
        this.path = path;
        this.bodyOnly = bodyOnly;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isBodyOnly() {
        return bodyOnly;
    }

    public void setBodyOnly(boolean bodyOnly) {
        this.bodyOnly = bodyOnly;
    }

    @Override
    public boolean isContinuable() {
        return false;
    }

    @Override
    public Asta4DPage produce() throws Exception {
        return new Asta4DPage(path, bodyOnly);
    }

    @Override
    public Class<? extends ContentWriter<Asta4DPage>> getContentWriter() {
        return Asta4DPageWriter.class;
    }

}
