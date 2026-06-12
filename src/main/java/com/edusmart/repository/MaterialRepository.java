package com.edusmart.repository;

import com.edusmart.model.Course;
import com.edusmart.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    List<Material> findByCourseOrderByOrderIndexAsc(Course course);
    long countByCourse(Course course);
}
