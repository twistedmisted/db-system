package ua.zxz.multydbsysytem.dto.table;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TableDto {

    private Long id;
    private Long dbId;
    private String name;
    private List<ColumnDto> columns = new ArrayList<>();
}
