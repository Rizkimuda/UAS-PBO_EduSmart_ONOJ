package com.edusmart.repository;

import com.edusmart.model.Certificate;
import com.edusmart.model.Course;
import com.edusmart.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    Optional<Certificate> findByUserAndCourse(User user, Course course);
    Optional<Certificate> findByCertificateCode(String certificateCode);
    List<Certificate> findByUser(User user);
}
