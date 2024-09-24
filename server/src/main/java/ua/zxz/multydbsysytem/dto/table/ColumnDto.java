package ua.zxz.multydbsysytem.dto.table;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ColumnDto {

    private String name;
    private ColumnType type;
    private List<ColumnConstraint> constraints = new ArrayList<>();
    private ForeignTableDto foreignTable;
}
