package com.payment.userservice.controller;

import com.payment.userservice.dto.AuthRequest;
import com.payment.userservice.dto.AuthResponse;
import com.payment.userservice.service.AuthService;
import com.payment.userservice.service.JwtService;
import com.payment.userservice.service.TokenBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        tokenBlacklistService.blacklist(token, jwtService.extractExpiry(token));
        return ResponseEntity.ok(new AuthResponse(true, "Logged out successfully", null));
    }
}
