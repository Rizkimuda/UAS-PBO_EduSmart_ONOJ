package com.edusmart.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "courses")
public class Course extends BaseEntity {

    @NotBlank(message = "Course title cannot be blank")
    @Size(min = 3, max = 100, message = "Course title must be between 3 and 100 characters")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Course description cannot be blank")
    @Lob
    @Column(nullable = false)
    private String description;

    @NotBlank(message = "Course category cannot be blank")
    @Column(nullable = false)
    private String category;

    @NotNull(message = "Instructor cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id", nullable = false)
    private User instructor;

    @NotNull(message = "Course status cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseStatus status = CourseStatus.DRAFT;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    public Course() {}

    public Course(String title, String description, String category, User instructor, CourseStatus status) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.instructor = instructor;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public User getInstructor() {
        return instructor;
    }

    public void setInstructor(User instructor) {
        this.instructor = instructor;
    }

    public CourseStatus getStatus() {
        return status;
    }

    public void setStatus(CourseStatus status) {
        this.status = status;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
}
