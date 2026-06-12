package com.edusmart.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "materials")
public class Material extends BaseEntity {

    @NotBlank(message = "Material title cannot be blank")
    @Size(min = 3, max = 100, message = "Material title must be between 3 and 100 characters")
    @Column(nullable = false)
    private String title;

    @NotNull(message = "Material type cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaterialType type;

    @NotBlank(message = "Material content cannot be blank")
    @Lob
    @Column(nullable = false)
    private String content;

    @Min(value = 1, message = "Order index must be at least 1")
    @Column(name = "order_index", nullable = false)
    private int orderIndex;

    @NotNull(message = "Course cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    public Material() {}

    public Material(String title, MaterialType type, String content, int orderIndex, Course course) {
        this.title = title;
        this.type = type;
        this.content = content;
        this.orderIndex = orderIndex;
        this.course = course;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public MaterialType getType() {
        return type;
    }

    public void setType(MaterialType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
