package com.edusmart.service.impl;

import com.edusmart.model.Certificate;
import com.edusmart.model.Course;
import com.edusmart.model.User;
import com.edusmart.repository.CertificateRepository;
import com.edusmart.repository.CourseRepository;
import com.edusmart.repository.UserRepository;
import com.edusmart.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class CertificateServiceImpl implements CertificateService {

    private final CertificateRepository certificateRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Autowired
    public CertificateServiceImpl(CertificateRepository certificateRepository,
                                  UserRepository userRepository,
                                  CourseRepository courseRepository) {
        this.certificateRepository = certificateRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public Certificate generateCertificate(Long userId, Long courseId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        Optional<Certificate> existing = certificateRepository.findByUserAndCourse(user, course);
        if (existing.isPresent()) {
            return existing.get();
        }

        String certCode = "CERT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String simulatedPath = "certificates/" + certCode + ".pdf";

        Certificate certificate = new Certificate(user, course, certCode, simulatedPath);
        return certificateRepository.save(certificate);
    }

    @Override
    public Optional<Certificate> getCertificate(Long userId, Long courseId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        return certificateRepository.findByUserAndCourse(user, course);
    }

    @Override
    public List<Certificate> getCertificatesByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return certificateRepository.findByUser(user);
    }
}
