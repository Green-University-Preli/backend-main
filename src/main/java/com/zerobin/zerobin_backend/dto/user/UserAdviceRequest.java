package com.zerobin.zerobin_backend.dto.user;

import lombok.Data;

@Data
public class UserAdviceRequest {
    private String title;        // Optional title for the advice/suggestion bundle
    private String adviceText;   // Plain text or JSON string with tips & suggestions
    private Double score;        // Optional score/weight from AI
}
