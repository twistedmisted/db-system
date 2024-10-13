package ua.zxz.multydbsysytem.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ua.zxz.multydbsysytem.dto.table.TableDto;
import ua.zxz.multydbsysytem.service.TableService;
import ua.zxz.multydbsysytem.web.payload.table.CrateTablePayload;
import ua.zxz.multydbsysytem.web.payload.table.TableUpdateRequest;

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
    public ResponseEntity<Object> createTable(@RequestBody CrateTablePayload crateTablePayload, Principal principal) {
        tableService.create(crateTablePayload, principal.getName());
        return new ResponseEntity<>(OK);
    }

    @PutMapping
    public ResponseEntity<Object> updateTable(@RequestBody TableUpdateRequest tablePayload, Principal principal) {
        TableDto tableDto = new TableDto();
        tableDto.setId(tablePayload.getId());
        tableDto.setName(tablePayload.getName());
        tableDto.setDbId(tablePayload.getDbId());
        tableService.update(tableDto, principal.getName());
        return new ResponseEntity<>(OK);
    }

    @DeleteMapping("/{tableId}")
    public ResponseEntity<Object> deleteTable(@PathVariable final Long tableId,
                                              @RequestParam Long dbId,
                                              Principal principal) {
        tableService.deleteById(tableId, dbId, principal.getName());
        return new ResponseEntity<>(OK);
    }
}
