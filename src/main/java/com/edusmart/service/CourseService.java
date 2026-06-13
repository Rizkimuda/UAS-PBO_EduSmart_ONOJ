package com.edusmart.service;

import com.edusmart.model.Course;
import com.edusmart.model.Material;
import com.edusmart.model.CourseStatus;
import java.util.List;
import java.util.Optional;

public interface CourseService {
    Course createCourse(Course course);
    Course updateCourse(Course course);
    void deleteCourse(Long courseId);
    Optional<Course> getCourseById(Long id);
    List<Course> getAllCourses();
    List<Course> getCoursesByStatus(CourseStatus status);
    List<Course> getCoursesByInstructor(Long instructorId);
    
    Material addMaterial(Long courseId, Material material);
    void deleteMaterial(Long materialId);
    Optional<Material> getMaterialById(Long materialId);
    
    void markMaterialAsComplete(Long userId, Long materialId);
}
