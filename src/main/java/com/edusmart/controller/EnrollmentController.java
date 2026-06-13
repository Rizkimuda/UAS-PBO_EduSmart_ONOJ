package com.edusmart.controller;

import com.edusmart.model.Enrollment;
import com.edusmart.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @Autowired
    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping("/enroll/{courseId}")
    public ResponseEntity<Enrollment> enrollInCourse(
            @PathVariable Long courseId,
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId) {
        Long activeUserId = (userId != null) ? userId : headerUserId;
        if (activeUserId == null) {
            return ResponseEntity.badRequest().build();
        }
        return new ResponseEntity<>(enrollmentService.enrollInCourse(activeUserId, courseId), HttpStatus.CREATED);
    }

    @GetMapping("/my")
    public ResponseEntity<List<Enrollment>> getMyEnrollments(
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId) {
        Long activeUserId = (userId != null) ? userId : headerUserId;
        if (activeUserId == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByUser(activeUserId));
    }

    @GetMapping("/{courseId}/progress")
    public ResponseEntity<Integer> getCourseProgress(
            @PathVariable Long courseId,
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId) {
        Long activeUserId = (userId != null) ? userId : headerUserId;
        if (activeUserId == null) {
            return ResponseEntity.badRequest().build();
        }
        return enrollmentService.getEnrollment(activeUserId, courseId)
                .map(e -> ResponseEntity.ok(e.getProgressPercent()))
                .orElse(ResponseEntity.notFound().build());
    }
}
