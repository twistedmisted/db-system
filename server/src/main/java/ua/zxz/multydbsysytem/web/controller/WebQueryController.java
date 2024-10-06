package ua.zxz.multydbsysytem.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.zxz.multydbsysytem.service.QueryService;
import ua.zxz.multydbsysytem.service.TableService;
import ua.zxz.multydbsysytem.web.payload.query.Condition;
import ua.zxz.multydbsysytem.web.payload.query.UpdateQueryRequest;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/web-queries")
@RequiredArgsConstructor
public class WebQueryController {

    private final TableService tableService;
    private final QueryService queryService;

    @GetMapping("/getByPK/{tableId}")
    public ResponseEntity<Object> getByPK(@PathVariable Long tableId,
                                          @RequestParam String name,
                                          @RequestParam Object value,
                                          Principal principal) {
        if (!tableService.hasRights(tableId, principal.getName())) {
            return new ResponseEntity<>(Map.of("message", "Can't get table record"), HttpStatus.BAD_REQUEST);
        }
        List<Object> records = queryService.getByColumn(tableId, new Condition(name, Condition.Operator.EQUALS, value));
        if (records.isEmpty()) {
            return new ResponseEntity<>(Map.of("message", "Can't get table record"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(records.getFirst(), OK);
    }

    @GetMapping("/getAll/{tableId}")
    public ResponseEntity<Object> getAll(@PathVariable Long tableId, Principal principal) {
        if (tableService.hasRights(tableId, principal.getName())) {
            return new ResponseEntity<>(queryService.getAll(tableId), HttpStatus.OK);
        }
        return new ResponseEntity<>(Map.of("message", "Can't get table content"), HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/save/{tableId}")
    public ResponseEntity<Object> save(@PathVariable Long tableId,
                                       @RequestBody Map<String, Object> object,
                                       Principal principal) {
        if (!tableService.hasRights(tableId, principal.getName())) {
            return new ResponseEntity<>(Map.of("message", "Can't save table record"), HttpStatus.BAD_REQUEST);
        }
        queryService.save(tableId, object);
        return new ResponseEntity<>(Map.of("message", "Record successfully saved"), OK);
    }

    @PutMapping("/update/{tableId}")
    public ResponseEntity<Object> update(@PathVariable Long tableId,
                                         @RequestBody UpdateQueryRequest updateQueryRequest,
                                         Principal principal) {
        if (!tableService.hasRights(tableId, principal.getName())) {
            return new ResponseEntity<>(Map.of("message", "Can't update table record"), HttpStatus.BAD_REQUEST);
        }
        queryService.update(tableId, updateQueryRequest);
        return new ResponseEntity<>(Map.of("message", "Record updated"), OK);
    }

    @DeleteMapping("/delete/{tableId}")
    public ResponseEntity<Object> delete(@PathVariable Long tableId,
                                         @RequestBody Map<String, Object> object,
                                         Principal principal) {
        if (!tableService.hasRights(tableId, principal.getName())) {
            return new ResponseEntity<>(Map.of("message", "Can't delete table record"), HttpStatus.BAD_REQUEST);
        }
        queryService.delete(tableId, object);
        return new ResponseEntity<>(Map.of("message", "Record successfully deleted"), OK);
    }
}
