package com.edusmart.controller;

import com.edusmart.dto.CourseRequest;
import com.edusmart.model.*;
import com.edusmart.service.CourseService;
import com.edusmart.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    private User instructor;
    private User student;

    @BeforeEach
    public void setUp() {
        instructor = new User("cinst", "cinst@edusmart.com", "password", "ROLE_INSTRUCTOR");
        instructor = userService.registerUser(instructor);

        student = new User("cstud", "cstud@edusmart.com", "password", "ROLE_STUDENT");
        student = userService.registerUser(student);
    }

    @Test
    public void testCreateAndGetCourses() throws Exception {
        CourseRequest req = new CourseRequest("Java API", "Spring Web API course", "Programming", instructor.getId(), "PUBLISHED", "thumbnail.png");

        mockMvc.perform(post("/api/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Java API"))
                .andExpect(jsonPath("$.category").value("Programming"));

        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").exists());
    }
}
