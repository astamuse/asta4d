package com.astamuse.asta4d.web.form;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializableFormDeserializer implements FormDeSerializer {

    @Override
    public byte[] serialize(Object form) {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream(1000);
            ObjectOutputStream oos = new ObjectOutputStream(byteOut);
            oos.writeObject(form);
            oos.close();
            return byteOut.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object deserialize(byte[] bytes) {
        try {
            ByteArrayInputStream byteIn = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(byteIn);
            Object obj = ois.readObject();
            ois.close();
            return obj;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
