package com.edusmart.repository;

import com.edusmart.model.Course;
import com.edusmart.model.CourseStatus;
import com.edusmart.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByStatus(CourseStatus status);
    List<Course> findByInstructor(User instructor);
    List<Course> findByCategoryAndStatus(String category, CourseStatus status);
}
