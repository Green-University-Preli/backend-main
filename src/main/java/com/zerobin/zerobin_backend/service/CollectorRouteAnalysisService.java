package com.zerobin.zerobin_backend.service;

import com.zerobin.zerobin_backend.dto.collector.CollectorRouteAnalysisRequest;
import com.zerobin.zerobin_backend.entity.User;
import com.zerobin.zerobin_backend.entity.collector.CollectorRouteAnalysis;
import com.zerobin.zerobin_backend.repository.CollectorRouteAnalysisRepository;
import com.zerobin.zerobin_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CollectorRouteAnalysisService {

    private final CollectorRouteAnalysisRepository analysisRepository;
    private final UserRepository userRepository;

    @Transactional
    public CollectorRouteAnalysis create(String userEmail, CollectorRouteAnalysisRequest req) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        CollectorRouteAnalysis entity = CollectorRouteAnalysis.builder()
                .title(req.getTitle())
                .analysisData(req.getAnalysisData())
                .priorityScore(req.getPriorityScore())
                .createdAt(LocalDateTime.now())
                .createdBy(user)
                .build();
        return analysisRepository.save(entity);
    }

    @Transactional(readOnly = true)
    public List<CollectorRouteAnalysis> listMine(String userEmail) {
        return analysisRepository.findByCreatedByEmailOrderByCreatedAtDesc(userEmail);
    }

    @Transactional(readOnly = true)
    public Optional<CollectorRouteAnalysis> getLatestMine(String userEmail) {
        return analysisRepository.findTopByCreatedByEmailOrderByCreatedAtDesc(userEmail);
    }
}
