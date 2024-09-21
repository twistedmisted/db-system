package ua.zxz.multydbsysytem.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.zxz.multydbsysytem.dto.DbDto;
import ua.zxz.multydbsysytem.dto.DbTokenDto;
import ua.zxz.multydbsysytem.dto.UserDto;
import ua.zxz.multydbsysytem.service.DbService;
import ua.zxz.multydbsysytem.web.payload.DbCreateRequest;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.Map;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/dbs")
@RequiredArgsConstructor
public class DbController {

    private final DbService dbService;

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(Map.of("result", dbService.getById(id)), OK);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(@RequestParam int pageNum, @RequestParam int pageSize, Principal principal) {
        return new ResponseEntity<>(Map.of("result", dbService.getAll(pageNum, pageSize, principal.getName())), OK);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody DbCreateRequest dbCreateRequest, Principal principal) {
        DbDto dbDto = new DbDto();
        dbDto.setName(dbCreateRequest.getDbName());
        dbDto.setStatus(dbCreateRequest.getDbStatus());
        dbDto.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        dbDto.setUser(new UserDto(principal.getName()));
        dbDto.setToken(new DbTokenDto(dbCreateRequest.getTokenLifeTime()));
        dbService.saveDb(dbDto);
        return new ResponseEntity<>(CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id,
                                         @Valid @RequestBody DbCreateRequest dbCreateRequest,
                                         Principal principal) {
        DbDto dbDto = new DbDto();
        dbDto.setId(id);
        dbDto.setName(dbCreateRequest.getDbName());
        dbDto.setStatus(dbCreateRequest.getDbStatus());
        dbDto.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        dbDto.setUser(new UserDto(principal.getName()));
        dbDto.setToken(new DbTokenDto(dbCreateRequest.getTokenLifeTime()));
        dbService.updateDb(dbDto);
        return new ResponseEntity<>(OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id, Principal principal) {
        dbService.removeDbById(id, principal.getName());
        return new ResponseEntity<>(OK);
    }
}
