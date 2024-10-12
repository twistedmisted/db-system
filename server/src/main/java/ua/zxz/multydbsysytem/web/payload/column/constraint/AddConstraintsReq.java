package ua.zxz.multydbsysytem.web.payload.column.constraint;

import lombok.Data;
import ua.zxz.multydbsysytem.dto.table.Constraints;

@Data
public class AddConstraintsReq {

    private String columnName;
    private Constraints constraints;
}
