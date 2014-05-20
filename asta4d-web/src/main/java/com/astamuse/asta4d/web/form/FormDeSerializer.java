package com.astamuse.asta4d.web.form;

public interface FormDeSerializer {

    public byte[] serialize(Object form);

    public Object deserialize(byte[] bytes);
}
