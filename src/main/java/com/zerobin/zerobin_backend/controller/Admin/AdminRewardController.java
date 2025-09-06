package com.zerobin.zerobin_backend.controller.Admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zerobin.zerobin_backend.dto.reward.RewardCreateRequest;
import com.zerobin.zerobin_backend.entity.reward.Reward;
import com.zerobin.zerobin_backend.security.JwtUtil;
import com.zerobin.zerobin_backend.service.RewardService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/rewards")
@Slf4j
public class AdminRewardController {

    private final RewardService rewardService;
    private final JwtUtil jwtUtil;

    private boolean isAdminRole(String role) {
        return role != null && ("ADMIN".equalsIgnoreCase(role)
                || "ROLE_ADMIN".equalsIgnoreCase(role)
                || role.toUpperCase().contains("ADMIN"));
    }

    // Admin: create a reward
    @PostMapping
    public ResponseEntity<?> createReward(HttpServletRequest httpRequest, @RequestBody RewardCreateRequest body) {
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
        String role = jwtUtil.getRoleFromToken(token);
        if (!isAdminRole(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden: admin role required");
        }
        Reward created = rewardService.createReward(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Admin: list all rewards
    @GetMapping
    public ResponseEntity<?> listAllRewards(HttpServletRequest httpRequest) {
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
        String role = jwtUtil.getRoleFromToken(token);
        if (!isAdminRole(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden: admin role required");
        }
        List<Reward> all = rewardService.listAllRewards();
        return ResponseEntity.ok(all);
    }

    // Admin: delete reward
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReward(HttpServletRequest httpRequest, @PathVariable Long id) {
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
        String role = jwtUtil.getRoleFromToken(token);
        if (!isAdminRole(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden: admin role required");
        }
        rewardService.deleteReward(id);
        return ResponseEntity.noContent().build();
    }
}
