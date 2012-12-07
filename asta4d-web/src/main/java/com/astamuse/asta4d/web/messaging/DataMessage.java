/*
 * Copyright 2012 astamuse company,Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

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
