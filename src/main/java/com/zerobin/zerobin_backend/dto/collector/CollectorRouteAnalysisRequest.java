package com.zerobin.zerobin_backend.dto.collector;

import lombok.Data;

@Data
public class CollectorRouteAnalysisRequest {
    private String title;              // Optional short title for the analysis
    private String analysisData;       // JSON string containing the AI suggestion: ordered zones, reasons, distances, etc.
    private Double priorityScore;      // Optional overall priority score assigned by AI
}
