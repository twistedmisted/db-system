package ua.zxz.multydbsysytem.dto.table;

import lombok.Data;

@Data
public class ForeignTableDto {

    private String tableName;
    private String columnName;
    private String constraintName;
    private String tableColumn;
}
