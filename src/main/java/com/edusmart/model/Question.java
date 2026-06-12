package com.edusmart.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "questions")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "q_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Question extends BaseEntity implements QuestionEvaluator {

    @NotBlank(message = "Question text cannot be blank")
    @Lob
    @Column(name = "question_text", nullable = false)
    private String questionText;

    @Min(value = 1, message = "Points must be at least 1")
    @Column(nullable = false)
    private int points;

    @NotBlank(message = "Correct answer cannot be blank")
    @Column(name = "correct_answer", nullable = false)
    private String correctAnswer;

    @Lob
    private String explanation;

    @NotNull(message = "Quiz cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    public Question() {}

    public Question(String questionText, int points, String correctAnswer, String explanation, Quiz quiz) {
        this.questionText = questionText;
        this.points = points;
        this.correctAnswer = correctAnswer;
        this.explanation = explanation;
        this.quiz = quiz;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    @Transient
    public abstract QuestionType getType();
}
