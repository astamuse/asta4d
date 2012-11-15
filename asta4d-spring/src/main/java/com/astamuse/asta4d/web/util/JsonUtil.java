package com.astamuse.asta4d.web.util;

import java.io.IOException;
import java.io.OutputStream;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {

    private final static ObjectMapper mapper = new ObjectMapper();
    static {
        // mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
    }

    public final static void toJson(OutputStream out, Object obj) throws IOException {
        mapper.writeValue(out, obj);
    }

    public final static String toJson(Object obj) throws JsonProcessingException {
        return mapper.writeValueAsString(obj);
    }

    public final static <T> T fromJson(String json, Class<T> cls) throws JsonParseException, JsonMappingException, IOException {
        return mapper.readValue(json, cls);
    }

    public final static Object fromJson(String json) throws JsonParseException, JsonMappingException, IOException {
        return mapper.readValue(json, Object.class);
    }

}
