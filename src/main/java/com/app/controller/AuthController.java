package com.app.controller;

import com.app.dto.ApiResponse;
import com.app.dto.LoginRequest;
import com.app.dto.LoginResponse;
import com.app.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(
                    ApiResponse.success("Login successful", response));
        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("Server is running", "OK"));
    }
}