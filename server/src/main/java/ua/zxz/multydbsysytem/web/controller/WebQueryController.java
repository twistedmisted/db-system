package ua.zxz.multydbsysytem.web.controller;

import static org.springframework.http.HttpStatus.OK;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.zxz.multydbsysytem.entity.TableEntity;
import ua.zxz.multydbsysytem.exception.WrongDataException;
import ua.zxz.multydbsysytem.repository.TableRepository;
import ua.zxz.multydbsysytem.service.DbService;
import ua.zxz.multydbsysytem.service.QueryService;
import ua.zxz.multydbsysytem.web.payload.query.Condition;
import ua.zxz.multydbsysytem.web.payload.query.ExecuteQueryReq;
import ua.zxz.multydbsysytem.web.payload.query.UpdateQueryRequest;

@RestController
@RequestMapping("/web-queries")
@RequiredArgsConstructor
public class WebQueryController {

  private final DbService dbService;
  private final TableRepository tableRepository;
  private final QueryService queryService;

  @GetMapping("/getByPK/{tableId}")
  public ResponseEntity<Object> getByPK(
      @PathVariable Long tableId,
      @RequestParam String name,
      @RequestParam Object value,
      Principal principal) {
    TableEntity tableEntity =
        tableRepository
            .findById(tableId)
            .orElseThrow(() -> new WrongDataException("Can't get table record"));
    if (!tableEntity.getDb().getUser().getUsername().equals(principal.getName())) {
      return new ResponseEntity<>(
          Map.of("message", "Can't get table record"), HttpStatus.BAD_REQUEST);
    }
    List<Object> records =
        queryService.getByColumn(
            tableEntity, new Condition(name, Condition.Operator.EQUALS, value));
    if (records.isEmpty()) {
      return new ResponseEntity<>(
          Map.of("message", "Can't get table record"), HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(records.getFirst(), OK);
  }

  @GetMapping("/getAll/{tableId}")
  public ResponseEntity<Object> getAll(@PathVariable Long tableId, Principal principal) {
    TableEntity tableEntity =
        tableRepository
            .findById(tableId)
            .orElseThrow(() -> new WrongDataException("Can't get table content"));
    if (!tableEntity.getDb().getUser().getUsername().equals(principal.getName())) {
      return new ResponseEntity<>(
          Map.of("message", "Can't get table content"), HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(queryService.getAll(tableEntity), HttpStatus.OK);
  }

  @PostMapping("/save/{tableId}")
  public ResponseEntity<Object> save(
      @PathVariable Long tableId, @RequestBody Map<String, Object> object, Principal principal) {
    TableEntity tableEntity =
        tableRepository
            .findById(tableId)
            .orElseThrow(() -> new WrongDataException("Can't save table record"));
    if (!tableEntity.getDb().getUser().getUsername().equals(principal.getName())) {
      return new ResponseEntity<>(
          Map.of("message", "Can't save table record"), HttpStatus.BAD_REQUEST);
    }
    queryService.save(tableEntity, object);
    return new ResponseEntity<>(Map.of("message", "Record successfully saved"), OK);
  }

  @PutMapping("/update/{tableId}")
  public ResponseEntity<Object> update(
      @PathVariable Long tableId,
      @RequestBody UpdateQueryRequest updateQueryRequest,
      Principal principal) {
    TableEntity tableEntity =
        tableRepository
            .findById(tableId)
            .orElseThrow(() -> new WrongDataException("Can't update table record"));
    if (!tableEntity.getDb().getUser().getUsername().equals(principal.getName())) {
      return new ResponseEntity<>(
          Map.of("message", "Can't update table record"), HttpStatus.BAD_REQUEST);
    }
    queryService.update(tableEntity, updateQueryRequest);
    return new ResponseEntity<>(Map.of("message", "Record updated"), OK);
  }

  @DeleteMapping("/delete/{tableId}")
  public ResponseEntity<Object> delete(
      @PathVariable Long tableId, @RequestBody Map<String, Object> object, Principal principal) {
    TableEntity tableEntity =
        tableRepository
            .findById(tableId)
            .orElseThrow(() -> new WrongDataException("Can't update table record"));
    if (!tableEntity.getDb().getUser().getUsername().equals(principal.getName())) {
      return new ResponseEntity<>(
          Map.of("message", "Can't delete table record"), HttpStatus.BAD_REQUEST);
    }
    queryService.delete(tableEntity, object);
    return new ResponseEntity<>(Map.of("message", "Record successfully deleted"), OK);
  }

  @PostMapping("/execute")
  public ResponseEntity<Object> execute(@RequestBody ExecuteQueryReq req, Principal principal) {
    if (!dbService.userHasRightsToDb(req.getDbId(), principal.getName())) {
      return new ResponseEntity<>(Map.of("message", "Can't execute query"), HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(queryService.execute(req), OK);
  }
}
