package ua.zxz.multydbsysytem.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ua.zxz.multydbsysytem.dto.DbTokenDto;
import ua.zxz.multydbsysytem.service.impl.DbTokenServiceImpl;
import ua.zxz.multydbsysytem.web.payload.DbTokenPayload;

import java.security.Principal;

import static org.springframework.http.HttpStatus.OK;

@Validated
@RestController
@RequestMapping("/dbTokens")
@RequiredArgsConstructor
public class DbTokenController {

    private final DbTokenServiceImpl dbTokenService;

    @GetMapping("/{dbId}")
    public ResponseEntity<DbTokenDto> getDbToken(@PathVariable("dbId") Long dbId, Principal principal) {
        return new ResponseEntity<>(dbTokenService.getById(dbId, principal.getName()), OK);
    }

    @PostMapping
    public ResponseEntity<Object> updateToken(@RequestBody DbTokenPayload dbTokenPayload, Principal principal) {
        dbTokenService.create(dbTokenPayload, principal.getName());
        return new ResponseEntity<>(OK);
    }
}
