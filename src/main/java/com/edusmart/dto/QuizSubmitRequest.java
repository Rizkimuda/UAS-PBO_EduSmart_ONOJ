package com.edusmart.dto;

import java.util.Map;

public class QuizSubmitRequest {
    private Map<Long, String> answers;

    public QuizSubmitRequest() {}

    public QuizSubmitRequest(Map<Long, String> answers) {
        this.answers = answers;
    }

    public Map<Long, String> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<Long, String> answers) {
        this.answers = answers;
    }
}
