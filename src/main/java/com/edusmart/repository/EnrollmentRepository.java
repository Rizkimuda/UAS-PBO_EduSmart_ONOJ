package com.edusmart.repository;

import com.edusmart.model.Course;
import com.edusmart.model.Enrollment;
import com.edusmart.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByUserAndCourse(User user, Course course);
    List<Enrollment> findByUser(User user);
    List<Enrollment> findByCourse(Course course);
    boolean existsByUserAndCourse(User user, Course course);
}
