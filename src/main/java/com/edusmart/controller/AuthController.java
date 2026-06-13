package com.edusmart.controller;

import com.edusmart.dto.LoginRequest;
import com.edusmart.dto.UserResponse;
import com.edusmart.model.User;
import com.edusmart.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody User user) {
        User registered = userService.registerUser(user);
        UserResponse response = new UserResponse(
                registered.getId(),
                registered.getUsername(),
                registered.getEmail(),
                registered.getRole(),
                registered.getRoleName(),
                registered.getPoints()
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        java.util.Optional<User> userOpt = userService.findByUsername(request.getUsername());
        if (userOpt.isPresent()) {
            User u = userOpt.get();
            if (passwordEncoder.matches(request.getPassword(), u.getPassword())) {
                UserResponse response = new UserResponse(
                        u.getId(),
                        u.getUsername(),
                        u.getEmail(),
                        u.getRole(),
                        u.getRoleName(),
                        u.getPoints()
                );
                return ResponseEntity.ok(response);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }
}
