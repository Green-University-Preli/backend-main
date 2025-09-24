package com.zerobin.zerobin_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zerobin.zerobin_backend.entity.CollectionVerification;

public interface CollectionVerificationRepository extends JpaRepository<CollectionVerification, Long> {
    // Paging support already inherited from JpaRepository (findAll(Pageable pageable))
}
