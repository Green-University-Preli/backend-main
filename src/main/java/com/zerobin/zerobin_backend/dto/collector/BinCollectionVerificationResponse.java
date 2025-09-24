package com.zerobin.zerobin_backend.dto.collector;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BinCollectionVerificationResponse {
    private Long id;
    private String binId;
    private Long collectorId;
    private String note;
    private String imageUrl;
    private LocalDateTime createdAt;
}
