package com.zerobin.zerobin_backend.dto.reward;

import lombok.Data;

@Data
public class RewardCreateRequest {
    private String title;
    private int points;
    private String description;
    private String longDescription;
    private String difficulty;
    private String category;
    private Boolean active; // optional, default true
}
