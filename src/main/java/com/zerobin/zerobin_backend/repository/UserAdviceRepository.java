package com.zerobin.zerobin_backend.repository;

import com.zerobin.zerobin_backend.entity.user.UserAdvice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAdviceRepository extends JpaRepository<UserAdvice, Long> {
    List<UserAdvice> findByCreatedByEmailOrderByCreatedAtDesc(String email);
    Optional<UserAdvice> findTopByCreatedByEmailOrderByCreatedAtDesc(String email);
}
