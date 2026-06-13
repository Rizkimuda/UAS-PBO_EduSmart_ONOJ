package com.edusmart.service.impl;

import com.edusmart.model.*;
import com.edusmart.repository.*;
import com.edusmart.service.CourseService;
import com.edusmart.service.GamificationService;
import com.edusmart.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final MaterialRepository materialRepository;
    private final MaterialCompletionRepository materialCompletionRepository;
    private final UserRepository userRepository;
    
    private GamificationService gamificationService;
    private EnrollmentService enrollmentService;

    @Autowired
    public CourseServiceImpl(CourseRepository courseRepository,
                             MaterialRepository materialRepository,
                             MaterialCompletionRepository materialCompletionRepository,
                             UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.materialRepository = materialRepository;
        this.materialCompletionRepository = materialCompletionRepository;
        this.userRepository = userRepository;
    }

    @Autowired
    public void setGamificationService(@Lazy GamificationService gamificationService) {
        this.gamificationService = gamificationService;
    }

    @Autowired
    public void setEnrollmentService(@Lazy EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @Override
    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    @Override
    public Course updateCourse(Course course) {
        return courseRepository.save(course);
    }

    @Override
    public void deleteCourse(Long courseId) {
        courseRepository.deleteById(courseId);
    }

    @Override
    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    @Override
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Override
    public List<Course> getCoursesByStatus(CourseStatus status) {
        return courseRepository.findByStatus(status);
    }

    @Override
    public List<Course> getCoursesByInstructor(Long instructorId) {
        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found"));
        return courseRepository.findByInstructor(instructor);
    }

    @Override
    public Material addMaterial(Long courseId, Material material) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        material.setCourse(course);
        return materialRepository.save(material);
    }

    @Override
    public void deleteMaterial(Long materialId) {
        materialRepository.deleteById(materialId);
    }

    @Override
    public Optional<Material> getMaterialById(Long materialId) {
        return materialRepository.findById(materialId);
    }

    @Override
    public void markMaterialAsComplete(Long userId, Long materialId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new IllegalArgumentException("Material not found"));

        if (!materialCompletionRepository.existsByUserAndMaterial(user, material)) {
            MaterialCompletion completion = new MaterialCompletion(user, material);
            materialCompletionRepository.save(completion);

            // Award points for material completion
            gamificationService.awardPoints(userId, 10);

            // Update enrollment progress
            enrollmentService.updateProgressPercent(userId, material.getCourse().getId());
        }
    }
}
