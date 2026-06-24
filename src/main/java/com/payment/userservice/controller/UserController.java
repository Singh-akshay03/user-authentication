package com.payment.userservice.controller;

import com.payment.userservice.dto.UserProfileResponse;
import com.payment.userservice.exception.UserNotFoundException;
import com.payment.userservice.model.User;
import com.payment.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User does not exist"));
        return ResponseEntity.ok(new UserProfileResponse(user.getId(), user.getEmail()));
    }
}
