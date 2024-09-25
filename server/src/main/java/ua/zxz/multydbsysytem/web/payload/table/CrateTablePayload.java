package ua.zxz.multydbsysytem.web.payload.table;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ua.zxz.multydbsysytem.dto.table.ColumnConstraint;
import ua.zxz.multydbsysytem.dto.table.ColumnType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class CrateTablePayload {

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
        @NotNull(message = "ColumnType can't be null")
        private ColumnType type;

        @JsonProperty("columnConstraints")
        private List<ColumnConstraint> constraints = new ArrayList<>();

        @JsonProperty("foreignTable")
        private ForeignTable foreignTable;

        private String defaultValue;

        public String getType() {
            return Objects.nonNull(type.getValue()) ?
                    type.getType() + "(" + type.getValue() + ")" :
                    type.getType().name();
        }
    }

    @Data
    public static class ForeignTable {

        @NotNull(message = "Foreign table name can't be null")
        @NotBlank(message = "Foreign table name can't be blank")
        private String tableName;

        @NotNull(message = "Foreign table column can't be null")
        @NotBlank(message = "Foreign table column can't be blank")
        private String columnName;
    }
}
