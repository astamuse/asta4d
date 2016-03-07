package com.astamuse.asta4d.web.util.data;

import java.io.IOException;
import java.io.OutputStream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class XmlUtil {
    // private static final xmlm
    private static final XmlMapper mapper = new XmlMapper();

    public final static void toXml(OutputStream out, Object obj) throws IOException {
        mapper.writeValue(out, obj);
    }

    public final static String toXml(Object obj) throws JsonProcessingException {
        return mapper.writeValueAsString(obj);
    }
}
