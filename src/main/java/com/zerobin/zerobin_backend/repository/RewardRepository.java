package com.zerobin.zerobin_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zerobin.zerobin_backend.entity.reward.Reward;

public interface RewardRepository extends JpaRepository<Reward, Long> {
    List<Reward> findAllByActiveTrue();
}
