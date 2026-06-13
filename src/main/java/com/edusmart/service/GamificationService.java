package com.edusmart.service;

import com.edusmart.model.Badge;
import com.edusmart.model.User;
import com.edusmart.model.UserBadge;
import java.util.List;

public interface GamificationService {
    void awardPoints(Long userId, int points);
    List<User> getLeaderboard();
    List<UserBadge> getBadgesEarned(Long userId);
    void checkAndAwardBadges(Long userId);
    Badge createBadge(Badge badge);
    List<Badge> getAllBadges();
}
