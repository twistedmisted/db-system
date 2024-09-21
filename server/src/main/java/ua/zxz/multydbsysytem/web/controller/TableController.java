package ua.zxz.multydbsysytem.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ua.zxz.multydbsysytem.dto.TableDto;
import ua.zxz.multydbsysytem.service.TableService;
import ua.zxz.multydbsysytem.web.payload.TablePayload;
import ua.zxz.multydbsysytem.web.payload.TableUpdateRequest;

import java.security.Principal;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@Validated
@RestController
@RequestMapping("/tables")
@RequiredArgsConstructor
public class TableController {

    private final TableService tableService;

    @GetMapping("/{tableId}")
    public ResponseEntity<Object> getTableById(@PathVariable final Long tableId, Principal principal) {
        return new ResponseEntity<>(Map.of("result", tableService.getTableById(tableId, principal.getName())), OK);
    }

    @GetMapping("/db/{dbId}")
    public ResponseEntity<Object> getTables(@PathVariable Long dbId, Principal principal) {
        return new ResponseEntity<>(Map.of("result", tableService.getAllTablesByDb(dbId, principal.getName())), OK);
    }

    @PostMapping
    public ResponseEntity<Object> createTable(@RequestBody TablePayload tablePayload, Principal principal) {
        tableService.create(tablePayload, principal.getName());
        return new ResponseEntity<>(OK);
    }

    @PutMapping
    public ResponseEntity<Object> updateTable(@RequestBody TableUpdateRequest tablePayload, Principal principal) {
        TableDto tableDto = new TableDto();
        tableDto.setId(tablePayload.getId());
        tableDto.setName(tablePayload.getName());
        tableService.update(tableDto, principal.getName());
        return new ResponseEntity<>(OK);
    }

    @DeleteMapping("/{tableId}")
    public ResponseEntity<Object> deleteTable(@PathVariable final Long tableId, Principal principal) {
        tableService.deleteById(tableId, principal.getName());
        return new ResponseEntity<>(OK);
    }
}
