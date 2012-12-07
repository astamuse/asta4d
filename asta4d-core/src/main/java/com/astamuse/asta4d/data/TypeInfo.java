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

package com.astamuse.asta4d.data;

class TypeInfo {
    private final Class<?> type;
    private final Object defaultValue;

    TypeInfo(Class<?> type) {
        // convert primitive types to their box types
        if (type.isPrimitive()) {
            String name = type.getName();
            switch (name) {
            case "char":
                this.type = Character.class;
                this.defaultValue = ' ';
                break;
            case "byte":
                this.type = Byte.class;
                this.defaultValue = Byte.valueOf((byte) 0);
                break;
            case "short":
                this.type = Short.class;
                this.defaultValue = Short.valueOf((short) 0);
                break;
            case "int":
                this.type = Integer.class;
                this.defaultValue = Integer.valueOf(0);
                break;
            case "long":
                this.type = Long.class;
                this.defaultValue = Long.valueOf(0L);
                break;
            case "float":
                this.type = Float.class;
                this.defaultValue = Float.valueOf(0.0F);
                break;
            case "double":
                this.type = Double.class;
                this.defaultValue = Double.valueOf(0.0D);
                break;
            case "boolean":
                this.type = Boolean.class;
                this.defaultValue = Boolean.FALSE;
                break;
            default:
                throw new IllegalArgumentException("Unexpected Primitive Type Name : " + name);
            }
        } else if (type.isArray() && type.getComponentType().isPrimitive()) {
            String name = type.getComponentType().getName();
            switch (name) {
            case "char":
                this.type = Character[].class;
                this.defaultValue = new Character[0];
                break;
            case "byte":
                this.type = Byte[].class;
                this.defaultValue = new Byte[0];
                break;
            case "short":
                this.type = Short[].class;
                this.defaultValue = new Short[0];
                break;
            case "int":
                this.type = Integer[].class;
                this.defaultValue = new Integer[0];
                break;
            case "long":
                this.type = Long[].class;
                this.defaultValue = new Long[0];
                break;
            case "float":
                this.type = Float[].class;
                this.defaultValue = new Float[0];
                break;
            case "double":
                this.type = Double[].class;
                this.defaultValue = new Double[0];
                break;
            case "boolean":
                this.type = Boolean[].class;
                this.defaultValue = new Boolean[0];
                break;
            default:
                throw new IllegalArgumentException("Unexpected Primitive Type Name : " + name);
            }
        } else {
            this.type = type;
            this.defaultValue = null;
        }
    }

    Class<?> getType() {
        return type;
    }

    Object getDefaultValue() {
        return defaultValue;
    }
}
