package ua.zxz.multydbsysytem.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ua.zxz.multydbsysytem.dto.UserDto;
import ua.zxz.multydbsysytem.exception.WrongDataException;
import ua.zxz.multydbsysytem.service.UserService;
import ua.zxz.multydbsysytem.service.security.JwtService;
import ua.zxz.multydbsysytem.service.security.RestorePasswordService;
import ua.zxz.multydbsysytem.web.payload.AuthenticationRequest;
import ua.zxz.multydbsysytem.web.payload.AuthenticationResponse;
import ua.zxz.multydbsysytem.web.payload.RegistrationRequest;
import ua.zxz.multydbsysytem.web.payload.RestorePasswordRequest;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RestorePasswordService restorePasswordService;

    @PostMapping(value = "/register")
    public ResponseEntity<Object> register(@Valid @RequestBody RegistrationRequest registrationRequest) {
        userService.saveUser(createFromRequest(registrationRequest));
        return new ResponseEntity<>(Map.of("message", "User successfully registered"), OK);
    }

    private UserDto createFromRequest(RegistrationRequest registrationRequest) {
        UserDto userDto = new UserDto();
        userDto.setFirstName(registrationRequest.getFirstName());
        userDto.setLastName(registrationRequest.getLastName());
        userDto.setUsername(registrationRequest.getUsername());
        userDto.setEmail(registrationRequest.getEmail());
        userDto.setPassword(registrationRequest.getPassword());
        return userDto;
    }

    @PostMapping(value = "/login", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthenticationResponse> authenticateUser(@RequestBody @Valid final AuthenticationRequest authenticationRequest) {
        Authentication authenticate;
        try {
            authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        } catch (final BadCredentialsException ex) {
            throw new WrongDataException("Email or password is incorrect");
        }
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setAccessToken(jwtService.generateToken(authenticate));
        return new ResponseEntity<>(authenticationResponse, OK);
    }

    @PostMapping("logout")
    public ResponseEntity<?> logout(@RequestHeader(AUTHORIZATION) String authHeader) {
        final String token = authHeader.substring(7);
        jwtService.invalidateTokenByUserToken(token);
        return new ResponseEntity<>(OK);
    }


    @PostMapping("/restore-password")
    public ResponseEntity<Map<String, Object>> restorePassword(@Valid @RequestBody RestorePasswordRequest restorePasswordRequest) {
        restorePasswordService.restorePasswordByEmail(restorePasswordRequest.getEmail());
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("detail", "Новий пароль надіслано на пошту.");
        return new ResponseEntity<>(responseBody, OK);
    }

    @GetMapping("/validate-token")
    public ResponseEntity<Object> validateToken(@RequestHeader(AUTHORIZATION) String authHeader) {
        final String token = authHeader.substring(7);
        return new ResponseEntity<>(Map.of("isValid", jwtService.validateToken(token)), OK);
    }
}
