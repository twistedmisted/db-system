package ua.zxz.multydbsysytem.web.payload.column;

import lombok.Data;

@Data
public class ModifyDataType {
    private String columnName;
    private String columnType;
}
