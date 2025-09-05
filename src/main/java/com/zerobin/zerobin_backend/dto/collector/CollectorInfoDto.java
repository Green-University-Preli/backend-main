package com.zerobin.zerobin_backend.dto.collector;

import lombok.Data;

@Data
public class CollectorInfoDto {

    private String assignedArea;
    private boolean isAvailable;
    private Long userId;

}
