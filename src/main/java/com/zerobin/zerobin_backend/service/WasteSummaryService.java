package com.zerobin.zerobin_backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.zerobin.zerobin_backend.dto.admin.WasteSummaryRequest;
import com.zerobin.zerobin_backend.entity.User;
import com.zerobin.zerobin_backend.entity.summary.WasteSummary;
import com.zerobin.zerobin_backend.repository.UserRepository;
import com.zerobin.zerobin_backend.repository.WasteSummaryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WasteSummaryService {

    private final WasteSummaryRepository wasteSummaryRepository;
    private final UserRepository userRepository;

    public WasteSummary createSummary(String adminEmail, WasteSummaryRequest req) {
        User admin = userRepository.findByEmail(adminEmail)
            .orElseThrow(() -> new RuntimeException("Admin user not found"));

        WasteSummary ws = WasteSummary.builder()
            .title(req.getTitle())
            .summaryText(req.getSummaryText())
            .totalReports(req.getTotalReports())
            .totalWeightKg(req.getTotalWeightKg())
            .periodStart(req.getPeriodStart())
            .periodEnd(req.getPeriodEnd())
            .createdAt(LocalDateTime.now())
            .createdBy(admin)
            .build();

        return wasteSummaryRepository.save(ws);
    }

    public List<WasteSummary> listAll() {
        return wasteSummaryRepository.findAllSorted();
    }
}
