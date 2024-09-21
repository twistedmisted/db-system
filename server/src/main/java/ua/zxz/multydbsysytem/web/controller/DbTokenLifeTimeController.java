package ua.zxz.multydbsysytem.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.zxz.multydbsysytem.dto.DbTokenLifeTime;

import java.util.Map;

@RestController
@RequestMapping("/dbTokenLifeTimes")
public class DbTokenLifeTimeController {

    @GetMapping
    public ResponseEntity<Object> getDbTokenLifeTimes() {
        return new ResponseEntity<>(Map.of("result", DbTokenLifeTime.values()), HttpStatus.OK);
    }
}
