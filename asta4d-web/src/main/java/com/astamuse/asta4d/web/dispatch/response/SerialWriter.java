package com.astamuse.asta4d.web.dispatch.response;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

public class SerialWriter implements ContentWriter {

    private List<ContentWriter> writerList;

    public SerialWriter(ContentWriter... writers) {
        writerList = Arrays.asList(writers);
    }

    @Override
    public void writeResponse(HttpServletResponse response, Object content) throws Exception {
        for (ContentWriter writer : writerList) {
            writer.writeResponse(response, content);
        }
    }

}
