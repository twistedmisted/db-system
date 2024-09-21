package ua.zxz.multydbsysytem.web.payload.query;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Data
public class Condition {

    private String columnName;
    private Operator operator;
    private Object value;

    @Getter
    @RequiredArgsConstructor
    public enum Operator {
        EQUALS(" = ");

        private final String symbol;

        @Override
        public String toString() {
            return symbol;
        }
    }
}
