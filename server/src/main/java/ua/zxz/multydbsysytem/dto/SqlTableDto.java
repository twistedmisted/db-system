package ua.zxz.multydbsysytem.dto;

import lombok.Data;

@Data
public class SqlTableDto {

    private String columnName;
    private Boolean isNullable;
    private String dataType;
    private String characterMaximumLength;
    private Boolean autoIncrement;
    private Boolean uniqueColumn;
    private Boolean primaryKey;
}
