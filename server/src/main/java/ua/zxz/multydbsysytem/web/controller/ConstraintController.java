package ua.zxz.multydbsysytem.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.zxz.multydbsysytem.dto.FieldSettings;

import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/constraints")
public class ConstraintController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> getConstraints() {
        Object result = Arrays.stream(FieldSettings.Setting.values())
                .map(c -> new FieldSettings(c, "dump"))
                .toList();
        return new ResponseEntity<>(
                Map.of("result", result),
                HttpStatus.OK
        );
    }
}
