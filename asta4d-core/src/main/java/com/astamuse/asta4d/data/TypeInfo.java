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
            case "boolean":
                this.type = Boolean.class;
                this.defaultValue = Boolean.FALSE;
                break;
            default:
                this.type = type;
                this.defaultValue = null;
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
            case "boolean":
                this.type = Boolean[].class;
                this.defaultValue = new Boolean[0];
                break;
            default:
                this.type = type;
                this.defaultValue = null;
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
