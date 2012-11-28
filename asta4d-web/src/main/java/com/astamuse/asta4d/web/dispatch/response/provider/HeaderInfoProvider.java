package com.astamuse.asta4d.web.dispatch.response.provider;

import com.astamuse.asta4d.web.dispatch.response.writer.ContentWriter;
import com.astamuse.asta4d.web.dispatch.response.writer.HeaderWriter;

public class HeaderInfoProvider implements ContentProvider<HeaderInfo> {

    private HeaderInfo info;

    public HeaderInfoProvider() {
        this(null);
    }

    public HeaderInfoProvider(HeaderInfo result) {
        this.info = result;
    }

    public HeaderInfo getInfo() {
        return info;
    }

    public void setInfo(HeaderInfo info) {
        this.info = info;
    }

    @Override
    public HeaderInfo produce() {
        return info;
    }

    @Override
    public boolean isContinuable() {
        return true;
    }

    @Override
    public Class<? extends ContentWriter<HeaderInfo>> getContentWriter() {
        return HeaderWriter.class;
    }
}
