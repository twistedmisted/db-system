package ua.zxz.multydbsysytem.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.zxz.multydbsysytem.dto.table.ColumnType;

import java.util.Map;

@RestController
@RequestMapping("/fieldTypes")
public class FieldTypeController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> getFieldTypes() {
        return new ResponseEntity<>(
                Map.of("result", ColumnType.Type.values()),
                HttpStatus.OK
        );
    }
}
