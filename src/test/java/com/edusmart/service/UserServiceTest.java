package com.edusmart.service;

import com.edusmart.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void testRegisterUser() {
        User user = new User("teststudent", "test@student.com", "password123", "ROLE_STUDENT");
        User registered = userService.registerUser(user);

        assertNotNull(registered.getId());
        assertEquals("teststudent", registered.getUsername());
        assertNotEquals("password123", registered.getPassword());
        assertEquals(0, registered.getPoints());
    }

    @Test
    public void testRegisterDuplicateUsername() {
        User user1 = new User("dupuser", "dup1@student.com", "password123", "ROLE_STUDENT");
        userService.registerUser(user1);

        User user2 = new User("dupuser", "dup2@student.com", "password123", "ROLE_STUDENT");
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(user2));
    }

    @Test
    public void testFindByUsername() {
        User user = new User("searchuser", "search@student.com", "password123", "ROLE_STUDENT");
        userService.registerUser(user);

        Optional<User> found = userService.findByUsername("searchuser");
        assertTrue(found.isPresent());
        assertEquals("search@student.com", found.get().getEmail());
    }

    @Test
    public void testUpdateProfile() {
        User user = new User("profileuser", "profile@student.com", "password123", "ROLE_STUDENT");
        user = userService.registerUser(user);

        User updated = userService.updateProfile(user.getId(), "newprofile@student.com", "newpassword");
        assertEquals("newprofile@student.com", updated.getEmail());
        assertNotEquals("newpassword", updated.getPassword());
    }
}
