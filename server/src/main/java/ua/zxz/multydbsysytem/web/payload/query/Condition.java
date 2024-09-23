package ua.zxz.multydbsysytem.web.payload.query;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Data
public class Condition {

    @NotBlank(message = "Column name can't be blank")
    @NotNull(message = "Column name can't be null")
    private String columnName;

    @NotNull(message = "Operator can't be null")
    private Operator operator;

    @NotNull(message = "Value can't be null")
    private Object value;

    @Getter
    @RequiredArgsConstructor
    public enum Operator {
        EQUALS(" = "),
        NOT_EQUALS(" != "),
        LOWER(" < "),
        GREATER(" > ");

        private final String symbol;

        @Override
        public String toString() {
            return symbol;
        }
    }
}
