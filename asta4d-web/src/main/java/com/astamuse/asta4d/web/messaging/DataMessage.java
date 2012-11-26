package com.astamuse.asta4d.web.messaging;

@SuppressWarnings("serial")
public class DataMessage implements Asta4dMessage {

    public enum DataType {
        JSON("json");
        private final String text;

        private DataType(String text) {
            this.text = text;
        }

        String getText() {
            return text;
        }
    }

    private final String data;
    private final DataType dataType;

    public DataMessage(String data, DataType dataType) {
        this.data = data;
        this.dataType = dataType;
    }

    public String getData() {
        return data;
    }

    public String getDataType() {
        return dataType.getText();
    }

    public String toJsonString() {
        // TODO use Json Library
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        sb.append("\"data\":" + getData());
        sb.append(',');
        sb.append("\"dataType\":\"" + getDataType() + "\"");
        sb.append('}');
        return sb.toString();
    }
}
