package ua.zxz.multydbsysytem.web.payload.table;

import lombok.Data;

@Data
public class TableUpdateRequest {

    private Long id;
    private String name;
    private Long dbId;
}
