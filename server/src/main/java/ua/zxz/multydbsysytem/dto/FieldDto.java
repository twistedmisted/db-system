package ua.zxz.multydbsysytem.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FieldDto {

    private String name;
    private FieldType type;
    private List<FieldSettings> constraints = new ArrayList<>();
}
