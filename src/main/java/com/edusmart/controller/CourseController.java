package com.edusmart.controller;

import com.edusmart.dto.CourseRequest;
import com.edusmart.model.Course;
import com.edusmart.model.CourseStatus;
import com.edusmart.model.Material;
import com.edusmart.model.User;
import com.edusmart.service.CourseService;
import com.edusmart.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;
    private final UserService userService;

    @Autowired
    public CourseController(CourseService courseService, UserService userService) {
        this.courseService = courseService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses(
            @RequestParam(required = false) CourseStatus status,
            @RequestParam(required = false) Long instructorId) {
        if (status != null) {
            return ResponseEntity.ok(courseService.getCoursesByStatus(status));
        }
        if (instructorId != null) {
            return ResponseEntity.ok(courseService.getCoursesByInstructor(instructorId));
        }
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @PostMapping
    public ResponseEntity<?> createCourse(@Valid @RequestBody CourseRequest request) {
        User instructor = userService.findById(request.getInstructorId())
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found"));
        CourseStatus status = CourseStatus.DRAFT;
        if (request.getStatus() != null) {
            status = CourseStatus.valueOf(request.getStatus().toUpperCase());
        }
        Course course = new Course(request.getTitle(), request.getDescription(), request.getCategory(), instructor, status);
        course.setThumbnailUrl(request.getThumbnailUrl());
        return new ResponseEntity<>(courseService.createCourse(course), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        return courseService.getCourseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{courseId}/materials")
    public ResponseEntity<Material> addMaterial(@PathVariable Long courseId, @Valid @RequestBody Material material) {
        return new ResponseEntity<>(courseService.addMaterial(courseId, material), HttpStatus.CREATED);
    }

    @PostMapping("/materials/{materialId}/complete")
    public ResponseEntity<Void> completeMaterial(
            @PathVariable Long materialId,
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId) {
        Long activeUserId = (userId != null) ? userId : headerUserId;
        if (activeUserId == null) {
            return ResponseEntity.badRequest().build();
        }
        courseService.markMaterialAsComplete(activeUserId, materialId);
        return ResponseEntity.ok().build();
    }
}
