package ua.zxz.multydbsysytem.dto.table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ForeignTableDto {

    @NotNull(message = "Is foreign table can't be null")
    private boolean foreignKey;

    @NotNull(message = "Foreign table name can't be null")
    @NotBlank(message = "Foreign table name can't be blank")
    private String tableName;

    @NotNull(message = "Foreign table column can't be null")
    @NotBlank(message = "Foreign table column can't be blank")
    private String columnName;

    private String constraintName;
    private String tableColumn;
}
