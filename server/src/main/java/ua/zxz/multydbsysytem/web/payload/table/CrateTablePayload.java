package ua.zxz.multydbsysytem.web.payload.table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ua.zxz.multydbsysytem.dto.table.ColumnDto;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CrateTablePayload {

    @NotNull(message = "Database can't be null")
    private Long dbId;

    @NotNull(message = "The table name can't be null")
    @Size(min = 1, max = 255, message = "The length of table name can be between 1 and 255 characters")
    private String name;

    @NotNull(message = "The tables fields can't be null")
    private List<ColumnDto> columns;

    @JsonIgnore
    public String getDbTechName() {
        return "id_" + dbId;
    }
}
