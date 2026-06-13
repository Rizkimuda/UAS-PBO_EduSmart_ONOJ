package com.edusmart.controller;

import com.edusmart.dto.QuizSubmitRequest;
import com.edusmart.model.Quiz;
import com.edusmart.model.QuizAttempt;
import com.edusmart.service.QuizService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;

    @Autowired
    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping
    public ResponseEntity<Quiz> createQuiz(@Valid @RequestBody Quiz quiz) {
        return new ResponseEntity<>(quizService.createQuiz(quiz), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Quiz> getQuizById(@PathVariable Long id) {
        return quizService.getQuizById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<Quiz> getQuizByCourseId(@PathVariable Long courseId) {
        return quizService.getQuizByCourseId(courseId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<QuizAttempt> submitQuiz(
            @PathVariable Long id,
            @RequestBody QuizSubmitRequest request,
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId) {
        Long activeUserId = (userId != null) ? userId : headerUserId;
        if (activeUserId == null) {
            return ResponseEntity.badRequest().build();
        }
        QuizAttempt attempt = quizService.submitQuizAttempt(activeUserId, id, request.getAnswers());
        return ResponseEntity.ok(attempt);
    }

    @GetMapping("/{id}/attempts")
    public ResponseEntity<List<QuizAttempt>> getQuizAttempts(
            @PathVariable Long id,
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId) {
        Long activeUserId = (userId != null) ? userId : headerUserId;
        if (activeUserId == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(quizService.getQuizAttempts(activeUserId, id));
    }
}
