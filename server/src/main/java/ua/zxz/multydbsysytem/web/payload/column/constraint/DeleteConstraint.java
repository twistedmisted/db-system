package ua.zxz.multydbsysytem.web.payload.column.constraint;

import lombok.Data;

@Data
public class DeleteConstraint {

    private String columnName;
    private String constraint;
}
