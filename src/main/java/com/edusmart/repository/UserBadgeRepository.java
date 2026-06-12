package com.edusmart.repository;

import com.edusmart.model.Badge;
import com.edusmart.model.User;
import com.edusmart.model.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {
    List<UserBadge> findByUser(User user);
    boolean existsByUserAndBadge(User user, Badge badge);
}
