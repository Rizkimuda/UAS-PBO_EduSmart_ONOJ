package com.edusmart.service.impl;

import com.edusmart.model.*;
import com.edusmart.repository.*;
import com.edusmart.service.GamificationService;
import com.edusmart.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final UserRepository userRepository;
    private final GamificationService gamificationService;

    @Autowired
    public QuizServiceImpl(QuizRepository quizRepository,
                           QuestionRepository questionRepository,
                           QuizAttemptRepository quizAttemptRepository,
                           UserRepository userRepository,
                           GamificationService gamificationService) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.userRepository = userRepository;
        this.gamificationService = gamificationService;
    }

    @Override
    public Quiz createQuiz(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    @Override
    public Optional<Quiz> getQuizById(Long id) {
        return quizRepository.findById(id);
    }

    @Override
    public Optional<Quiz> getQuizByCourseId(Long courseId) {
        return quizRepository.findAll().stream()
                .filter(q -> q.getCourse().getId().equals(courseId))
                .findFirst();
    }

    @Override
    public QuizAttempt submitQuizAttempt(Long userId, Long quizId, Map<Long, String> answers) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found"));

        long previousAttempts = quizAttemptRepository.countByUserAndQuiz(user, quiz);
        if (previousAttempts >= quiz.getMaxAttempts()) {
            throw new IllegalStateException("Maximum quiz attempts reached");
        }

        List<Question> questions = questionRepository.findByQuiz(quiz);
        int totalPointsPossible = 0;
        int scoreEarned = 0;

        for (Question question : questions) {
            totalPointsPossible += question.getPoints();
            String studentAnswer = answers.get(question.getId());
            if (studentAnswer != null && question.grade(studentAnswer)) {
                scoreEarned += question.getPoints();
            }
        }

        int finalScorePercent = totalPointsPossible > 0 ? (scoreEarned * 100) / totalPointsPossible : 0;
        boolean isPassed = finalScorePercent >= quiz.getPassingScore();

        QuizAttempt attempt = new QuizAttempt(quiz, user, finalScorePercent, isPassed, LocalDateTime.now());
        attempt = quizAttemptRepository.save(attempt);

        if (isPassed) {
            long passCount = quizAttemptRepository.countByUserAndQuizAndIsPassed(user, quiz, true);
            if (passCount == 1) {
                gamificationService.awardPoints(userId, 25);
            }
        }

        return attempt;
    }

    @Override
    public List<QuizAttempt> getQuizAttempts(Long userId, Long quizId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found"));
        return quizAttemptRepository.findByUserAndQuiz(user, quiz);
    }

    @Override
    public List<QuizAttempt> getQuizAttemptsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return quizAttemptRepository.findByUser(user);
    }
}
