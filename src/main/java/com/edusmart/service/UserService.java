package com.edusmart.service;

import com.edusmart.model.User;
import java.util.Optional;

public interface UserService {
    User registerUser(User user);
    Optional<User> findByUsername(String username);
    Optional<User> findById(Long id);
    User updateProfile(Long userId, String email, String password);
    void addPoints(Long userId, int points);
}
