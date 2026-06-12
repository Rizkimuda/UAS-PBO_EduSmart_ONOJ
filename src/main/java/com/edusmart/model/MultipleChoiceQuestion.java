package com.edusmart.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;

@Entity
@DiscriminatorValue("MULTIPLE_CHOICE")
public class MultipleChoiceQuestion extends Question {

    @NotBlank(message = "Option A cannot be blank")
    @Column(name = "option_a")
    private String optionA;

    @NotBlank(message = "Option B cannot be blank")
    @Column(name = "option_b")
    private String optionB;

    @NotBlank(message = "Option C cannot be blank")
    @Column(name = "option_c")
    private String optionC;

    @NotBlank(message = "Option D cannot be blank")
    @Column(name = "option_d")
    private String optionD;

    public MultipleChoiceQuestion() {}

    public MultipleChoiceQuestion(String questionText, int points, String correctAnswer, String explanation, Quiz quiz,
                                  String optionA, String optionB, String optionC, String optionD) {
        super(questionText, points, correctAnswer, explanation, quiz);
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    @Override
    public boolean grade(String studentAnswer) {
        if (studentAnswer == null) return false;
        return getCorrectAnswer().trim().equalsIgnoreCase(studentAnswer.trim());
    }

    @Override
    public QuestionType getType() {
        return QuestionType.MULTIPLE_CHOICE;
    }
}
