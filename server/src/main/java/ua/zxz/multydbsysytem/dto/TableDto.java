package ua.zxz.multydbsysytem.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TableDto {

    private Long id;
    private String name;
    private List<FieldDto> columns = new ArrayList<>();
}
