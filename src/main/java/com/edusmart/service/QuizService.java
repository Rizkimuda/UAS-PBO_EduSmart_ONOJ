package com.edusmart.service;

import com.edusmart.model.Quiz;
import com.edusmart.model.QuizAttempt;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface QuizService {
    Quiz createQuiz(Quiz quiz);
    Optional<Quiz> getQuizById(Long id);
    Optional<Quiz> getQuizByCourseId(Long courseId);
    
    QuizAttempt submitQuizAttempt(Long userId, Long quizId, Map<Long, String> answers);
    List<QuizAttempt> getQuizAttempts(Long userId, Long quizId);
    List<QuizAttempt> getQuizAttemptsByUser(Long userId);
}
