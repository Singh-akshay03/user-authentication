package com.payment.userservice.service;

import com.payment.userservice.dto.AuthRequest;
import com.payment.userservice.dto.AuthResponse;
import com.payment.userservice.exception.EmailAlreadyExistsException;
import com.payment.userservice.exception.InvalidPasswordException;
import com.payment.userservice.exception.UserNotFoundException;
import com.payment.userservice.model.User;
import com.payment.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse register(AuthRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already registered");
        }
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        userRepository.save(user);
        return new AuthResponse(true, "Registration successful");
    }

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User does not exist"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Wrong password");
        }
        return new AuthResponse(true, "Login successful");
    }
}
