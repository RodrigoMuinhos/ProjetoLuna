package com.luna.core.auth.controller;

import com.luna.core.auth.dto.FirstAdminRequest;
import com.luna.core.auth.dto.LoginRequest;
import com.luna.core.auth.dto.LoginResponse;
import com.luna.core.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @PostMapping("/first-admin")
    public ResponseEntity<Void> createFirstAdmin(@RequestBody @Valid FirstAdminRequest request) {
        authService.createFirstAdmin(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        if (request == null || request.email() == null || request.password() == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(authService.login(request));
    }
}
