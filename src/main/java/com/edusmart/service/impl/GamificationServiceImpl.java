package com.edusmart.service.impl;

import com.edusmart.model.Badge;
import com.edusmart.model.User;
import com.edusmart.model.UserBadge;
import com.edusmart.repository.BadgeRepository;
import com.edusmart.repository.UserBadgeRepository;
import com.edusmart.repository.UserRepository;
import com.edusmart.service.GamificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class GamificationServiceImpl implements GamificationService {

    private final UserRepository userRepository;
    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;

    @Autowired
    public GamificationServiceImpl(UserRepository userRepository,
                                   BadgeRepository badgeRepository,
                                   UserBadgeRepository userBadgeRepository) {
        this.userRepository = userRepository;
        this.badgeRepository = badgeRepository;
        this.userBadgeRepository = userBadgeRepository;
    }

    @Override
    public void awardPoints(Long userId, int points) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setPoints(user.getPoints() + points);
        userRepository.save(user);

        checkAndAwardBadges(userId);
    }

    @Override
    public List<User> getLeaderboard() {
        return userRepository.findAll(Sort.by(Sort.Direction.DESC, "points")).stream()
                .filter(u -> "ROLE_STUDENT".equals(u.getRole()))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserBadge> getBadgesEarned(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return userBadgeRepository.findByUser(user);
    }

    @Override
    public void checkAndAwardBadges(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Badge> allBadges = badgeRepository.findAll();
        for (Badge badge : allBadges) {
            if (user.getPoints() >= badge.getRequirementPoints()) {
                if (!userBadgeRepository.existsByUserAndBadge(user, badge)) {
                    UserBadge userBadge = new UserBadge(user, badge);
                    userBadgeRepository.save(userBadge);
                }
            }
        }
    }

    @Override
    public Badge createBadge(Badge badge) {
        return badgeRepository.save(badge);
    }

    @Override
    public List<Badge> getAllBadges() {
        return badgeRepository.findAll();
    }
}
