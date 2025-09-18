package com.zerobin.zerobin_backend.repository;

import com.zerobin.zerobin_backend.entity.collector.CollectorRouteAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CollectorRouteAnalysisRepository extends JpaRepository<CollectorRouteAnalysis, Long> {
    List<CollectorRouteAnalysis> findByCreatedByEmailOrderByCreatedAtDesc(String email);
    Optional<CollectorRouteAnalysis> findTopByCreatedByEmailOrderByCreatedAtDesc(String email);
}
