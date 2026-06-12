package com.edusmart.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "quizzes")
public class Quiz extends BaseEntity {

    @NotBlank(message = "Quiz title cannot be blank")
    @Size(min = 3, max = 100, message = "Quiz title must be between 3 and 100 characters")
    @Column(nullable = false)
    private String title;

    @Min(value = 1, message = "Time limit must be at least 1 minute")
    @Column(name = "time_limit", nullable = false)
    private int timeLimit; // in minutes

    @Min(value = 0, message = "Passing score cannot be negative")
    @Column(name = "passing_score", nullable = false)
    private int passingScore;

    @Min(value = 1, message = "Max attempts must be at least 1")
    @Column(name = "max_attempts", nullable = false)
    private int maxAttempts;

    @NotNull(message = "Course cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    public Quiz() {}

    public Quiz(String title, int timeLimit, int passingScore, int maxAttempts, Course course) {
        this.title = title;
        this.timeLimit = timeLimit;
        this.passingScore = passingScore;
        this.maxAttempts = maxAttempts;
        this.course = course;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public int getPassingScore() {
        return passingScore;
    }

    public void setPassingScore(int passingScore) {
        this.passingScore = passingScore;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
