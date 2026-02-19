package com.payrolltaxpro.controller;

import com.payrolltaxpro.dto.ApiResponse;
import com.payrolltaxpro.dto.LoginRequest;
import com.payrolltaxpro.dto.LoginResponse;
import com.payrolltaxpro.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt for user: {}", request.getUsername());
        Map<String, Object> response = authenticationService.authenticate(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        // For JWT-based authentication, logout is handled client-side by removing the token
        // This endpoint can be used for logout tracking if needed
        log.info("User logged out");
        return ResponseEntity.ok(ApiResponse.success("Logout successful", null));
    }
}
