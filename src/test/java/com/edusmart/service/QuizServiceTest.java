package com.edusmart.service;

import com.edusmart.model.*;
import com.edusmart.repository.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class QuizServiceTest {

    @Autowired
    private QuizService quizService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionRepository questionRepository;

    private User student;
    private Quiz quiz;
    private Question mcQuestion;
    private Question essayQuestion;

    @BeforeEach
    public void setUp() {
        User instructor = new User("inst2", "inst2@edusmart.com", "password", "ROLE_INSTRUCTOR");
        instructor = userService.registerUser(instructor);

        student = new User("stud2", "stud2@edusmart.com", "password", "ROLE_STUDENT");
        student = userService.registerUser(student);

        Course course = new Course("Spring Boot", "Learn Spring Boot framework", "Computer Science", instructor, CourseStatus.PUBLISHED);
        course = courseService.createCourse(course);

        quiz = new Quiz("OOP Concepts Quiz", 30, 70, 3, course);
        quiz = quizService.createQuiz(quiz);

        mcQuestion = new MultipleChoiceQuestion(
                "What is abstraction?", 50, "A", "Abstraction hides details", quiz,
                "Hiding implementation", "Showing implementation", "Inheriting fields", "Overriding methods"
        );
        mcQuestion = questionRepository.save(mcQuestion);

        essayQuestion = new EssayQuestion(
                "Explain encapsulation.", 50, "capsule", "Encapsulation wraps data", quiz
        );
        essayQuestion = questionRepository.save(essayQuestion);
    }

    @Test
    public void testSubmitQuizAttemptSuccess() {
        Map<Long, String> studentAnswers = new HashMap<>();
        studentAnswers.put(mcQuestion.getId(), "A");
        studentAnswers.put(essayQuestion.getId(), "It wraps things like a capsule.");

        QuizAttempt attempt = quizService.submitQuizAttempt(student.getId(), quiz.getId(), studentAnswers);
        assertNotNull(attempt.getId());
        assertEquals(100, attempt.getScore());
        assertTrue(attempt.isPassed());

        User updatedStudent = userService.findById(student.getId()).orElseThrow();
        assertEquals(25, updatedStudent.getPoints());
    }

    @Test
    public void testSubmitQuizAttemptFailed() {
        Map<Long, String> studentAnswers = new HashMap<>();
        studentAnswers.put(mcQuestion.getId(), "B");
        studentAnswers.put(essayQuestion.getId(), "I don't know.");

        QuizAttempt attempt = quizService.submitQuizAttempt(student.getId(), quiz.getId(), studentAnswers);
        assertNotNull(attempt.getId());
        assertEquals(0, attempt.getScore());
        assertFalse(attempt.isPassed());

        User updatedStudent = userService.findById(student.getId()).orElseThrow();
        assertEquals(0, updatedStudent.getPoints());
    }
}
