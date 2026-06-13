package com.edusmart.controller;

import com.edusmart.dto.QuizSubmitRequest;
import com.edusmart.model.*;
import com.edusmart.repository.QuestionRepository;
import com.edusmart.service.CourseService;
import com.edusmart.service.QuizService;
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

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class QuizControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private QuizService quizService;

    @Autowired
    private QuestionRepository questionRepository;

    private User student;
    private Quiz quiz;
    private Question question;

    @BeforeEach
    public void setUp() {
        User instructor = new User("qinst", "qinst@edusmart.com", "password", "ROLE_INSTRUCTOR");
        instructor = userService.registerUser(instructor);

        student = new User("qstud", "qstud@edusmart.com", "password", "ROLE_STUDENT");
        student = userService.registerUser(student);

        Course course = new Course("API Quiz Course", "Description", "Programming", instructor, CourseStatus.PUBLISHED);
        course = courseService.createCourse(course);

        quiz = new Quiz("Rest Quiz", 20, 70, 3, course);
        quiz = quizService.createQuiz(quiz);

        question = new MultipleChoiceQuestion("What is REST?", 100, "A", "REST explanation", quiz, "Option A", "Option B", "Option C", "Option D");
        question = questionRepository.save(question);
    }

    @Test
    public void testSubmitQuizAttemptViaApi() throws Exception {
        Map<Long, String> answers = new HashMap<>();
        answers.put(question.getId(), "A");
        QuizSubmitRequest req = new QuizSubmitRequest(answers);

        mockMvc.perform(post("/api/quizzes/" + quiz.getId() + "/submit")
                .param("userId", student.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(100))
                .andExpect(jsonPath("$.passed").value(true));

        mockMvc.perform(get("/api/quizzes/" + quiz.getId() + "/attempts")
                .param("userId", student.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].score").value(100));
    }
}
