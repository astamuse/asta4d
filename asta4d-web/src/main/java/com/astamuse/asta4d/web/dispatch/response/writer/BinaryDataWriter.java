package com.astamuse.asta4d.web.dispatch.response.writer;

import java.io.InputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;

public class BinaryDataWriter implements ContentWriter<InputStream> {
    @Override
    public void writeResponse(UrlMappingRule currentRule, HttpServletResponse response, InputStream content) throws Exception {
        try {
            byte[] bs = new byte[4096];
            ServletOutputStream out = response.getOutputStream();
            int len = 0;
            while ((len = content.read(bs)) != -1) {
                out.write(bs, 0, len);
            }
            // we do not need to close servlet output stream since the container
            // will close it.
        } finally {
            // since we have depleted this stream, there is no reason for not
            // closing it.
            content.close();
        }
    }

}
