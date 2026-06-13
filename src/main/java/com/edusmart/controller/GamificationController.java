package com.edusmart.controller;

import com.edusmart.model.Badge;
import com.edusmart.model.User;
import com.edusmart.model.UserBadge;
import com.edusmart.service.GamificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gamification")
public class GamificationController {

    private final GamificationService gamificationService;

    @Autowired
    public GamificationController(GamificationService gamificationService) {
        this.gamificationService = gamificationService;
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<User>> getLeaderboard() {
        return ResponseEntity.ok(gamificationService.getLeaderboard());
    }

    @GetMapping("/badges")
    public ResponseEntity<List<Badge>> getAllBadges() {
        return ResponseEntity.ok(gamificationService.getAllBadges());
    }

    @GetMapping("/my-badges")
    public ResponseEntity<List<UserBadge>> getMyBadges(
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId) {
        Long activeUserId = (userId != null) ? userId : headerUserId;
        if (activeUserId == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(gamificationService.getBadgesEarned(activeUserId));
    }
}
