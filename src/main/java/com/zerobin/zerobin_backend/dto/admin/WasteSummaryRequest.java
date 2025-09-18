package com.zerobin.zerobin_backend.dto.admin;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class WasteSummaryRequest {
    private String title;
    private String summaryText;
    private Integer totalReports;
    private Double totalWeightKg;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
}
