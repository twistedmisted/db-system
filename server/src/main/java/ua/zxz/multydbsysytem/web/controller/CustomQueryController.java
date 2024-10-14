package ua.zxz.multydbsysytem.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.zxz.multydbsysytem.dto.QueryDto;
import ua.zxz.multydbsysytem.service.CustomQueryService;
import ua.zxz.multydbsysytem.service.DbService;

import java.security.Principal;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/customQuery")
@RequiredArgsConstructor
public class CustomQueryController {

  private final CustomQueryService customQueryService;
  private final DbService dbService;

  @PostMapping
  public ResponseEntity<Object> save(@RequestBody QueryDto queryDto, Principal principal) {
    if (!dbService.userHasRightsToDb(queryDto.getDbId(), principal.getName())) {
      return new ResponseEntity<>(Map.of("message", "Can't save query"), HttpStatus.BAD_REQUEST);
    }
    customQueryService.save(queryDto);
    return new ResponseEntity<>(Map.of("message", "Query successfully saved"), OK);
  }
}
