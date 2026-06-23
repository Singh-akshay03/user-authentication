package com.payment.userservice.service;

import com.payment.userservice.dto.AuthRequest;
import com.payment.userservice.dto.AuthResponse;
import com.payment.userservice.model.User;
import com.payment.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public AuthResponse register(AuthRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        User user = User.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .build();
        userRepository.save(user);
        return new AuthResponse(true, "Registration successful");
    }

    public AuthResponse login(AuthRequest request) {
        boolean exists = userRepository
                .findByEmailAndPassword(request.getEmail(), request.getPassword())
                .isPresent();
        if (exists) {
            return new AuthResponse(true, "Login successful");
        }
        return new AuthResponse(false, "Invalid email or password");
    }
}
