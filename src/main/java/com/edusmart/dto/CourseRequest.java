package com.edusmart.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CourseRequest {

    @NotBlank(message = "Course title cannot be blank")
    @Size(min = 3, max = 100, message = "Course title must be between 3 and 100 characters")
    private String title;

    @NotBlank(message = "Course description cannot be blank")
    private String description;

    @NotBlank(message = "Course category cannot be blank")
    private String category;

    @NotNull(message = "Instructor ID cannot be null")
    private Long instructorId;

    private String status;

    private String thumbnailUrl;

    public CourseRequest() {}

    public CourseRequest(String title, String description, String category, Long instructorId, String status, String thumbnailUrl) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.instructorId = instructorId;
        this.status = status;
        this.thumbnailUrl = thumbnailUrl;
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

    public Long getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(Long instructorId) {
        this.instructorId = instructorId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
}
