package ua.zxz.multydbsysytem.web.payload.column;

import lombok.Data;

@Data
public class ModifyDefVal {
    private String columnName;
    private Object defVal;
}
