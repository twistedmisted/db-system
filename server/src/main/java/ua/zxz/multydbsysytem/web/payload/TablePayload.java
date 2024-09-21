package ua.zxz.multydbsysytem.web.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ua.zxz.multydbsysytem.dto.FieldSettings;
import ua.zxz.multydbsysytem.dto.FieldType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class TablePayload {

    @NotNull(message = "Database can't be null")
    private Long dbId;

    @NotNull(message = "The table name can't be null")
    @Size(min = 1, max = 255, message = "The length of table name can be between 1 and 255 characters")
    private String name;

    @NotNull(message = "The tables fields can't be null")
    private List<Column> columns;

    @Data
    public static class Column {
        @NotNull(message = "Column name can't be null")
        @Size(min = 1, max = 255, message = "The length of column name can be between 1 and 255 characters")
        private String name;

        @JsonProperty("columnType")
        @NotNull(message = "FieldType can't be null")
        private FieldType type;

        @JsonProperty("columnConstraints")
        private List<FieldSettings> settings = new ArrayList<>();

        public String getType() {
            return Objects.nonNull(type.getValue()) ?
                    type.getType() + "(" + type.getValue() + ")" :
                    type.getType().name();
        }
    }
}
