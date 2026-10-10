package com.maha.shoppingmart.controller;

import com.maha.shoppingmart.entity.User;
import com.maha.shoppingmart.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> data) {

        String email = data.get("email");
        String password = data.get("password");
        String confirmPassword = data.get("confirmPassword");

        if (email == null || email.isBlank()
                || password == null || password.isBlank()
                || confirmPassword == null || confirmPassword.isBlank()) {

            return ResponseEntity.badRequest()
                    .body(Map.of("message", "All fields are required"));
        }

        email = email.trim().toLowerCase();

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Enter a valid email address"));
        }

        if (password.length() < 8) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Password must contain at least 8 characters"));
        }

        if (!password.equals(confirmPassword)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Passwords do not match"));
        }

        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Email is already registered"));
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Registration successful"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> data) {

        String email = data.get("email");
        String password = data.get("password");

        if (email == null || email.isBlank()
                || password == null || password.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Email and password are required"));
        }

        email = email.trim().toLowerCase();

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid email or password"));
        }

        return ResponseEntity.ok(
                Map.of("message", "Login successful", "email", user.getEmail())
        );
    }
}
