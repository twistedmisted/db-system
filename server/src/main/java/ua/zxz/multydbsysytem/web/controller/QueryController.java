package ua.zxz.multydbsysytem.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ua.zxz.multydbsysytem.service.QueryService;
import ua.zxz.multydbsysytem.web.payload.query.Condition;
import ua.zxz.multydbsysytem.web.payload.query.UpdateQueryRequest;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@Validated
@RestController
@RequestMapping("/queries")
@RequiredArgsConstructor
public class QueryController {

    private final QueryService queryService;

    @GetMapping("/{tableName}/getByColumn")
    public ResponseEntity<Object> getByColumn(@PathVariable("tableName") String tableName,
                                              @RequestBody Condition request,
                                              Principal principal) {
        Map<String, Object> body = new HashMap<>();
        body.put("objects", queryService.getByColumn(Long.parseLong(principal.getName()), tableName, request));
        return new ResponseEntity<>(body, OK);
    }

    @GetMapping("/{tableName}/getAll")
    public ResponseEntity<Object> getById(@PathVariable("tableName") String tableName, Principal principal) {
        Map<String, Object> body = new HashMap<>();
        body.put("objects", queryService.getAll(Long.parseLong(principal.getName()), tableName));
        return new ResponseEntity<>(body, OK);
    }

    @PostMapping("/{tableName}/save")
    public ResponseEntity<Object> save(@PathVariable("tableName") String tableName,
                                       @RequestBody Map<String, Object> object,
                                       Principal principal) {
        queryService.save(Long.parseLong(principal.getName()), tableName, object);
        return new ResponseEntity<>(Map.of("result", "Saved"), OK);
    }

    @PutMapping("/{tableName}/update")
    public ResponseEntity<Object> update(@PathVariable("tableName") String tableName,
                                         @RequestBody UpdateQueryRequest updateQueryRequest,
                                         Principal principal) {
        queryService.update(Long.parseLong(principal.getName()), tableName, updateQueryRequest);
        return new ResponseEntity<>(Map.of("result", "Updated"), OK);
    }

    @DeleteMapping("/{tableName}/delete")
    public ResponseEntity<Object> delete(@PathVariable("tableName") String tableName,
                                         @RequestBody Condition condition,
                                         Principal principal) {
        queryService.delete(Long.parseLong(principal.getName()), tableName, condition);
        return new ResponseEntity<>(Map.of("result", "Removed"), OK);
    }
}
