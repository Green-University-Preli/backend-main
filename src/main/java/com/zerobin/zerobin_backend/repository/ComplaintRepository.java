package com.zerobin.zerobin_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zerobin.zerobin_backend.entity.Complaint;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByUserId(Long userId);
}
