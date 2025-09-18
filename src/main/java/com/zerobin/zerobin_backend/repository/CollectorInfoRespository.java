package com.zerobin.zerobin_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zerobin.zerobin_backend.entity.collector.CollectorInfo;

public interface CollectorInfoRespository extends JpaRepository<CollectorInfo, Long> {

    // Query by nested property: CollectorInfo.user.role
    List<CollectorInfo> findByUser_Role(String role);

}
