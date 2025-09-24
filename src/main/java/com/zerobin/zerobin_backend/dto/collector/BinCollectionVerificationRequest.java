package com.zerobin.zerobin_backend.dto.collector;

import lombok.Data;

// Used for JSON body alternative when not sending multipart image (if needed)
@Data
public class BinCollectionVerificationRequest {
    private String binId;
    private String note; // short note from collector
    private String imageUrl; // fallback if image already uploaded somewhere
}
