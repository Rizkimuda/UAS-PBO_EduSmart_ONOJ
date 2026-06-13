package com.edusmart.service.impl;

import com.edusmart.model.Course;
import com.edusmart.model.Enrollment;
import com.edusmart.model.User;
import com.edusmart.repository.CourseRepository;
import com.edusmart.repository.EnrollmentRepository;
import com.edusmart.repository.MaterialCompletionRepository;
import com.edusmart.repository.MaterialRepository;
import com.edusmart.repository.UserRepository;
import com.edusmart.service.CertificateService;
import com.edusmart.service.EnrollmentService;
import com.edusmart.service.GamificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final MaterialRepository materialRepository;
    private final MaterialCompletionRepository materialCompletionRepository;

    private GamificationService gamificationService;
    private CertificateService certificateService;

    @Autowired
    public EnrollmentServiceImpl(EnrollmentRepository enrollmentRepository,
                                 UserRepository userRepository,
                                 CourseRepository courseRepository,
                                 MaterialRepository materialRepository,
                                 MaterialCompletionRepository materialCompletionRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.materialRepository = materialRepository;
        this.materialCompletionRepository = materialCompletionRepository;
    }

    @Autowired
    public void setGamificationService(@Lazy GamificationService gamificationService) {
        this.gamificationService = gamificationService;
    }

    @Autowired
    public void setCertificateService(@Lazy CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @Override
    public Enrollment enrollInCourse(Long userId, Long courseId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        Optional<Enrollment> existing = enrollmentRepository.findByUserAndCourse(user, course);
        if (existing.isPresent()) {
            return existing.get();
        }

        Enrollment enrollment = new Enrollment(user, course);
        return enrollmentRepository.save(enrollment);
    }

    @Override
    public Optional<Enrollment> getEnrollment(Long userId, Long courseId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        return enrollmentRepository.findByUserAndCourse(user, course);
    }

    @Override
    public List<Enrollment> getEnrollmentsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return enrollmentRepository.findByUser(user);
    }

    @Override
    public List<Enrollment> getEnrollmentsByCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        return enrollmentRepository.findByCourse(course);
    }

    @Override
    public boolean isEnrolled(Long userId, Long courseId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        return enrollmentRepository.existsByUserAndCourse(user, course);
    }

    @Override
    public void updateProgressPercent(Long userId, Long courseId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        Enrollment enrollment = enrollmentRepository.findByUserAndCourse(user, course)
                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found"));

        long totalMaterials = materialRepository.countByCourse(course);
        if (totalMaterials == 0) {
            enrollment.setProgressPercent(0);
            enrollmentRepository.save(enrollment);
            return;
        }

        long completedMaterials = materialCompletionRepository.countByUserAndCourse(user, course);
        int progress = (int) ((completedMaterials * 100) / totalMaterials);
        enrollment.setProgressPercent(progress);

        if (progress == 100 && enrollment.getCompletedAt() == null) {
            enrollment.setCompletedAt(LocalDateTime.now());
            // Award course completion points
            gamificationService.awardPoints(userId, 100);
            // Generate certificate
            certificateService.generateCertificate(userId, courseId);
        }

        enrollmentRepository.save(enrollment);
    }
}
