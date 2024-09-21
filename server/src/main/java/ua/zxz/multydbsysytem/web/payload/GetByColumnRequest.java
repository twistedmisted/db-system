package ua.zxz.multydbsysytem.web.payload;

import lombok.Data;

@Data
public class GetByColumnRequest {

    private String columnName;
    private Object value;
}
