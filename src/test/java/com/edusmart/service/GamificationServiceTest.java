package com.edusmart.service;

import com.edusmart.model.Badge;
import com.edusmart.model.User;
import com.edusmart.model.UserBadge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class GamificationServiceTest {

    @Autowired
    private GamificationService gamificationService;

    @Autowired
    private UserService userService;

    private User student1;
    private User student2;
    private Badge badge1;
    private Badge badge2;

    @BeforeEach
    public void setUp() {
        student1 = new User("gstud1", "gstud1@edusmart.com", "password", "ROLE_STUDENT");
        student1 = userService.registerUser(student1);

        student2 = new User("gstud2", "gstud2@edusmart.com", "password", "ROLE_STUDENT");
        student2 = userService.registerUser(student2);

        badge1 = new Badge("Pioneer", "First milestones", "icon1.png", 10);
        badge1 = gamificationService.createBadge(badge1);

        badge2 = new Badge("Scholar", "Advanced learning", "icon2.png", 50);
        badge2 = gamificationService.createBadge(badge2);
    }

    @Test
    public void testAwardPointsAndBadges() {
        assertEquals(0, student1.getPoints());
        List<UserBadge> earnedInitial = gamificationService.getBadgesEarned(student1.getId());
        assertTrue(earnedInitial.isEmpty());

        gamificationService.awardPoints(student1.getId(), 15);

        User updatedStudent = userService.findById(student1.getId()).orElseThrow();
        assertEquals(15, updatedStudent.getPoints());

        List<UserBadge> earned = gamificationService.getBadgesEarned(student1.getId());
        assertEquals(1, earned.size());
        assertEquals("Pioneer", earned.get(0).getBadge().getName());

        gamificationService.awardPoints(student1.getId(), 40);

        List<UserBadge> earnedAfter = gamificationService.getBadgesEarned(student1.getId());
        assertEquals(2, earnedAfter.size());
    }

    @Test
    public void testLeaderboard() {
        gamificationService.awardPoints(student1.getId(), 100);
        gamificationService.awardPoints(student2.getId(), 200);

        List<User> leaderboard = gamificationService.getLeaderboard();
        assertTrue(leaderboard.size() >= 2);
        
        int index1 = -1;
        int index2 = -1;
        for (int i = 0; i < leaderboard.size(); i++) {
            if (leaderboard.get(i).getId().equals(student1.getId())) index1 = i;
            if (leaderboard.get(i).getId().equals(student2.getId())) index2 = i;
        }

        assertTrue(index2 < index1);
    }
}
