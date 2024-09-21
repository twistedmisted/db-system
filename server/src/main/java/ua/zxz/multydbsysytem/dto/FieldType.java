package ua.zxz.multydbsysytem.dto;

import lombok.Data;

@Data
public class FieldType {

    private Type type;
    private String value = null;

    public enum Type {
        INT, BIGINT, FLOAT, DOUBLE, CHAR, VARCHAR, BLOB, BOOLEAN, DATETIME, TIME, TIMESTAMP;

        public static Type fromString(String type) {
            return valueOf(type.toUpperCase());
        }
    }
}
