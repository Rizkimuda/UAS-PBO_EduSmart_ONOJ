package com.edusmart.repository;

import com.edusmart.model.Course;
import com.edusmart.model.Material;
import com.edusmart.model.MaterialCompletion;
import com.edusmart.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MaterialCompletionRepository extends JpaRepository<MaterialCompletion, Long> {
    Optional<MaterialCompletion> findByUserAndMaterial(User user, Material material);
    boolean existsByUserAndMaterial(User user, Material material);

    @Query("SELECT COUNT(mc) FROM MaterialCompletion mc WHERE mc.user = :user AND mc.material.course = :course")
    long countByUserAndCourse(@Param("user") User user, @Param("course") Course course);
}
