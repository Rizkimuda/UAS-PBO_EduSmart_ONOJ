package com.edusmart.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ESSAY")
public class EssayQuestion extends Question {

    public EssayQuestion() {}

    public EssayQuestion(String questionText, int points, String correctAnswer, String explanation, Quiz quiz) {
        super(questionText, points, correctAnswer, explanation, quiz);
    }

    @Override
    public boolean grade(String studentAnswer) {
        if (studentAnswer == null) return false;
        // Simple essay grading: case-insensitive check if user answer contains the correct keyword
        return studentAnswer.toLowerCase().trim().contains(getCorrectAnswer().toLowerCase().trim());
    }

    @Override
    public QuestionType getType() {
        return QuestionType.ESSAY;
    }
}
