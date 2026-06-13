package com.edusmart.service;

import com.edusmart.model.Enrollment;
import java.util.List;
import java.util.Optional;

public interface EnrollmentService {
    Enrollment enrollInCourse(Long userId, Long courseId);
    Optional<Enrollment> getEnrollment(Long userId, Long courseId);
    List<Enrollment> getEnrollmentsByUser(Long userId);
    List<Enrollment> getEnrollmentsByCourse(Long courseId);
    boolean isEnrolled(Long userId, Long courseId);
    void updateProgressPercent(Long userId, Long courseId);
}
