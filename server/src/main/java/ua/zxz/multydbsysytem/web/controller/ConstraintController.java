package ua.zxz.multydbsysytem.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.zxz.multydbsysytem.service.ConstraintService;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/constraints")
@RequiredArgsConstructor
public class ConstraintController {

//    @GetMapping
//    public ResponseEntity<Map<String, Object>> getConstraints() {
//        Object result = Arrays.stream(Constraints.values())
//                .map(c -> new ColumnConstraint(c, "dump"))
//                .toList();
//        return new ResponseEntity<>(
//                Map.of("result", result),
//                HttpStatus.OK
//        );
//    }

    private final ConstraintService constraintService;

    @GetMapping("/getByTableAndColumn")
    public ResponseEntity<Map<String, Object>> getByTableAndColumn(@RequestParam Long tableId,
                                                           @RequestParam String columnName,
                                                           Principal principal) {
        return new ResponseEntity<>(Map.of("result", constraintService.getByTableId(tableId, columnName, principal.getName())), HttpStatus.OK);
    }
}
