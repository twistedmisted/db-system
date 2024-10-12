package ua.zxz.multydbsysytem.web.payload.column.constraint;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ua.zxz.multydbsysytem.dto.table.ForeignTableDto;

@Data
public class ConstraintReq {

    private String columnName;
    private String constraint;

    @JsonProperty("foreignTable")
    private ForeignTableDto foreignTable;
}
