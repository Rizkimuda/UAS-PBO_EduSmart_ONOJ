package com.edusmart.repository;

import com.edusmart.model.Quiz;
import com.edusmart.model.QuizAttempt;
import com.edusmart.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    List<QuizAttempt> findByUserAndQuiz(User user, Quiz quiz);
    List<QuizAttempt> findByUser(User user);
    long countByUserAndQuizAndIsPassed(User user, Quiz quiz, boolean isPassed);
    long countByUserAndQuiz(User user, Quiz quiz);
}
