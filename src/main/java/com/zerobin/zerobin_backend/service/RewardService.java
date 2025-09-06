package com.zerobin.zerobin_backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.zerobin.zerobin_backend.dto.reward.RewardCreateRequest;
import com.zerobin.zerobin_backend.entity.User;
import com.zerobin.zerobin_backend.entity.reward.Reward;
import com.zerobin.zerobin_backend.entity.reward.RewardClaim;
import com.zerobin.zerobin_backend.repository.RewardClaimRepository;
import com.zerobin.zerobin_backend.repository.RewardRepository;
import com.zerobin.zerobin_backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RewardService {

    private final RewardRepository rewardRepository;
    private final RewardClaimRepository rewardClaimRepository;
    private final UserRepository userRepository;

    // Admin: create reward
    public Reward createReward(RewardCreateRequest req) {
        Reward reward = Reward.builder()
                .title(req.getTitle())
                .points(req.getPoints())
                .description(req.getDescription())
                .longDescription(req.getLongDescription())
                .difficulty(req.getDifficulty())
                .category(req.getCategory())
                .active(req.getActive() == null ? true : req.getActive())
                .createdAt(LocalDateTime.now())
                .build();
        return rewardRepository.save(reward);
    }

    // Admin: delete reward
    public void deleteReward(Long rewardId) {
        rewardRepository.deleteById(rewardId);
    }

    // Admin: list all rewards (active and inactive)
    public List<Reward> listAllRewards() {
        return rewardRepository.findAll();
    }

    // User/Admin: list active rewards
    public List<Reward> listActiveRewards() {
        return rewardRepository.findAllByActiveTrue();
    }

    // User: claim reward if enough points and not already claimed
    public RewardClaim claimReward(String userEmail, Long rewardId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new RuntimeException("Reward not found"));
        if (!reward.isActive()) {
            throw new IllegalStateException("Reward is not active");
        }
        if (user.getPoints() < reward.getPoints()) {
            throw new IllegalArgumentException("Not enough points to claim this reward");
        }
        RewardClaim existing = rewardClaimRepository.findFirstByUser_IdAndReward_Id(user.getId(), rewardId);
        if (existing != null) {
            throw new IllegalStateException("Reward already claimed");
        }
        // Deduct points and create claim
        user.setPoints(user.getPoints() - reward.getPoints());
        userRepository.save(user);

        RewardClaim claim = RewardClaim.builder()
                .user(user)
                .reward(reward)
                .status("claimed")
                .dateEarned(LocalDateTime.now())
                .build();
        return rewardClaimRepository.save(claim);
    }

    // User: list my claims
    public List<RewardClaim> listUserClaims(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return rewardClaimRepository.findByUser_Id(user.getId());
    }
}
