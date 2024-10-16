package ua.zxz.multydbsysytem.web.controller;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

import java.security.Principal;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.zxz.multydbsysytem.dto.QueryDto;
import ua.zxz.multydbsysytem.service.CustomQueryService;
import ua.zxz.multydbsysytem.service.DbService;

@RestController
@RequestMapping("/customQuery")
@RequiredArgsConstructor
public class CustomQueryController {

  private final CustomQueryService customQueryService;
  private final DbService dbService;

  @GetMapping("/{id}")
  public ResponseEntity<Object> getCustomQuery(
      @PathVariable("id") Long queryId, @RequestParam Long dbId, Principal principal) {
    if (!dbService.userHasRightsToDb(dbId, principal.getName())) {
      return new ResponseEntity<>(Map.of("message", "Can't get query"), BAD_REQUEST);
    }
    return new ResponseEntity<>(Map.of("result", customQueryService.getById(queryId)), OK);
  }

  @GetMapping
  public ResponseEntity<Object> getAll(@RequestParam Long dbId, Principal principal) {
    if (!dbService.userHasRightsToDb(dbId, principal.getName())) {
      return new ResponseEntity<>(Map.of("message", "Can't get all queries"), BAD_REQUEST);
    }
    return new ResponseEntity<>(Map.of("result", customQueryService.getAllByDb(dbId)), OK);
  }

  @PostMapping
  public ResponseEntity<Object> save(@RequestBody QueryDto queryDto, Principal principal) {
    if (!dbService.userHasRightsToDb(queryDto.getDbId(), principal.getName())) {
      return new ResponseEntity<>(Map.of("message", "Can't save query"), BAD_REQUEST);
    }
    customQueryService.save(queryDto);
    return new ResponseEntity<>(Map.of("message", "Query successfully saved"), OK);
  }

  @PutMapping
  public ResponseEntity<Object> update(@RequestBody QueryDto queryDto, Principal principal) {
    if (!dbService.userHasRightsToDb(queryDto.getDbId(), principal.getName())) {
      return new ResponseEntity<>(Map.of("message", "Can't update query"), BAD_REQUEST);
    }
    customQueryService.update(queryDto);
    return new ResponseEntity<>(Map.of("message", "Query successfully updated"), OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Object> delete(@PathVariable Long id, @RequestParam Long dbId, Principal principal) {
    if (!dbService.userHasRightsToDb(dbId, principal.getName())) {
      return new ResponseEntity<>(Map.of("message", "Can't delete query"), BAD_REQUEST);
    }
    customQueryService.delete(id);
    return new ResponseEntity<>(Map.of("message", "Query deleted"), OK);
  }
}
