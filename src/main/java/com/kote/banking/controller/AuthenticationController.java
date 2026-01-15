package com.kote.banking.controller;

import com.kote.banking.dto.AuthenticationRequestDto;
import com.kote.banking.dto.AuthenticationResponseDto;
import com.kote.banking.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponseDto> register(@RequestBody AuthenticationRequestDto dto){
        AuthenticationResponseDto register = authenticationService.register(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(register.getId())
                .toUri();
        return ResponseEntity.created(location).body(register);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDto> login(@RequestBody AuthenticationRequestDto dto){
        AuthenticationResponseDto login = authenticationService.login(dto);
        return ResponseEntity.ok(login);
    }

}
