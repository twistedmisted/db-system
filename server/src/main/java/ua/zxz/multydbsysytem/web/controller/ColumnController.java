package ua.zxz.multydbsysytem.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ua.zxz.multydbsysytem.dto.table.ColumnDto;
import ua.zxz.multydbsysytem.service.ColumnService;
import ua.zxz.multydbsysytem.web.payload.ColumnNamePayload;

import java.security.Principal;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@Validated
@RestController
@RequestMapping("/columns")
@RequiredArgsConstructor
public class ColumnController {

    private final ColumnService columnService;

    @GetMapping("/{columnName}")
    public ResponseEntity<Object> getColumnByName(@PathVariable String columnName,
                                                  @RequestParam Long tableId,
                                                  Principal principal) {
        return new ResponseEntity<>(
                Map.of("result", columnService.getColumnByName(tableId, columnName, principal.getName())),
                OK
        );
    }

    @PostMapping("/addColumn")
    public ResponseEntity<Object> addNewColumn(@RequestParam Long tableId,
                                               @RequestBody ColumnDto column,
                                               Principal principal) {
        columnService.addNewColumn(tableId, column, principal.getName());
        return new ResponseEntity<>(Map.of("message", "Column successfully added"), OK);
    }

    @PostMapping("/deleteColumn")
    public ResponseEntity<Object> deleteColumn(@RequestParam Long tableId,
                                               @RequestBody ColumnNamePayload columnNamePayload,
                                               Principal principal) {
        columnService.deleteColumn(tableId, columnNamePayload.getName(), principal.getName());
        return new ResponseEntity<>(Map.of("message", "Column successfully removed"), OK);
    }
}
