package ua.zxz.multydbsysytem.dto.table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Objects;

@Data
public class ColumnDto {

    @NotNull(message = "Column name can't be null")
    @Size(min = 1, max = 255, message = "The length of column name can be between 1 and 255 characters")
    private String name;

    @JsonProperty("columnType")
    @NotNull(message = "ColumnType can't be null")
    private ColumnType type;

    @JsonProperty("constraints")
    @NotNull(message = "Column constraints can't be null")
    private Constraints constraints = new Constraints();

    private String defaultValue;

    @JsonIgnore
    public String getType() {
        return Objects.nonNull(type.getValue()) ?
                type.getType().getSqlName() + "(" + type.getValue() + ")" :
                type.getType().getSqlName();
    }
}
