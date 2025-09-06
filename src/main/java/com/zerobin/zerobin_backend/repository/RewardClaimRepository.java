package com.zerobin.zerobin_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zerobin.zerobin_backend.entity.reward.RewardClaim;

public interface RewardClaimRepository extends JpaRepository<RewardClaim, Long> {
    List<RewardClaim> findByUser_Id(Long userId);
    List<RewardClaim> findByReward_Id(Long rewardId);
    RewardClaim findFirstByUser_IdAndReward_Id(Long userId, Long rewardId);
}
