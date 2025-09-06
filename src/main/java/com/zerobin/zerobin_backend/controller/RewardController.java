package com.zerobin.zerobin_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zerobin.zerobin_backend.entity.reward.Reward;
import com.zerobin.zerobin_backend.entity.reward.RewardClaim;
import com.zerobin.zerobin_backend.security.JwtUtil;
import com.zerobin.zerobin_backend.service.RewardService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rewards")
public class RewardController {

    private final RewardService rewardService;
    private final JwtUtil jwtUtil;

    // Public: list active rewards (no auth required)
    @GetMapping
    public ResponseEntity<List<Reward>> listActiveRewards() {
        List<Reward> rewards = rewardService.listActiveRewards();
        return ResponseEntity.ok(rewards);
    }

    // User: claim a reward
    @PostMapping("/{rewardId}/claim")
    public ResponseEntity<?> claimReward(HttpServletRequest httpRequest, @PathVariable Long rewardId) {
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
        String email = jwtUtil.getEmailFromToken(token);
        try {
            RewardClaim claim = rewardService.claimReward(email, rewardId);
            return ResponseEntity.ok(claim);
        } catch (IllegalArgumentException iae) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(iae.getMessage());
        } catch (IllegalStateException ise) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ise.getMessage());
        } catch (RuntimeException re) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(re.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error");
        }
    }

    // User: list my reward claims
    @GetMapping("/me/claims")
    public ResponseEntity<?> listMyClaims(HttpServletRequest httpRequest) {
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
        String email = jwtUtil.getEmailFromToken(token);
        List<RewardClaim> claims = rewardService.listUserClaims(email);
        return ResponseEntity.ok(claims);
    }
}
