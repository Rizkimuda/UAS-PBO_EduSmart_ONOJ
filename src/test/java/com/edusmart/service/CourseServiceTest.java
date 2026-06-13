package com.edusmart.service;

import com.edusmart.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class CourseServiceTest {

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    @Autowired
    private EnrollmentService enrollmentService;

    private User instructor;
    private User student;
    private Course course;

    @BeforeEach
    public void setUp() {
        instructor = new User("inst1", "inst1@edusmart.com", "password", "ROLE_INSTRUCTOR");
        instructor = userService.registerUser(instructor);

        student = new User("stud1", "stud1@edusmart.com", "password", "ROLE_STUDENT");
        student = userService.registerUser(student);

        course = new Course("Java OOP", "Learn Object Oriented Programming", "Computer Science", instructor, CourseStatus.PUBLISHED);
        course = courseService.createCourse(course);
    }

    @Test
    public void testCreateAndGetCourse() {
        assertNotNull(course.getId());
        Optional<Course> found = courseService.getCourseById(course.getId());
        assertTrue(found.isPresent());
        assertEquals("Java OOP", found.get().getTitle());
    }

    @Test
    public void testAddAndCompleteMaterial() {
        Material mat1 = new Material("Inheritance", MaterialType.TEXT, "Content about Inheritance", 1, course);
        mat1 = courseService.addMaterial(course.getId(), mat1);
        assertNotNull(mat1.getId());

        Material mat2 = new Material("Polymorphism", MaterialType.TEXT, "Content about Polymorphism", 2, course);
        mat2 = courseService.addMaterial(course.getId(), mat2);

        // Enroll student first
        Enrollment enrollment = enrollmentService.enrollInCourse(student.getId(), course.getId());
        assertEquals(0, enrollment.getProgressPercent());

        // Mark mat1 as complete
        courseService.markMaterialAsComplete(student.getId(), mat1.getId());

        // Student points should increase by 10
        User updatedStudent = userService.findById(student.getId()).orElseThrow();
        assertEquals(10, updatedStudent.getPoints());

        // Enrollment progress should update to 50%
        Enrollment updatedEnrollment = enrollmentService.getEnrollment(student.getId(), course.getId()).orElseThrow();
        assertEquals(50, updatedEnrollment.getProgressPercent());
    }
}
