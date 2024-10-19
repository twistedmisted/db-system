package ua.zxz.multydbsysytem.dto.table;

import lombok.Data;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class ColumnType {

    private Type type;
    private String value = null;

    @Getter
    public enum Type {
        INT("INTEGER"),
        BIGINT("BIGINT"),
//        FLOAT("FLOAT"),
        DOUBLE("DOUBLE"),
        CHAR("CHAR"),
        VARCHAR("VARCHAR"),
        //        BLOB("BLOB"),
        BOOLEAN("BIT"),
        DATE("DATE"),
        DATETIME("DATETIME"),
        TIME("TIME"),
        TIMESTAMP("TIMESTAMP");

        private final String sqlName;
        private static final Map<String, Type> TYPES_BY_SQL_NAME;

        static {
            TYPES_BY_SQL_NAME = Arrays.stream(values()).collect(Collectors.toMap(v -> v.sqlName, v -> v));
        }

        Type(String sqlName) {
            this.sqlName = sqlName;
        }

        public static Type fromString(String type) {
            Type value = TYPES_BY_SQL_NAME.get(type.toUpperCase());
            if (value == null) {
                throw new IllegalArgumentException("Unknown SQL type: " + type);
            }
            return value;
        }
    }
}
