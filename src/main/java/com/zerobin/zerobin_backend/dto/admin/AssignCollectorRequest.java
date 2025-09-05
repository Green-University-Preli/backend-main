package com.zerobin.zerobin_backend.dto.admin;

import java.util.List;

import lombok.Data;

@Data
public class AssignCollectorRequest {
    private Long collectorUserId;
    private List<Long> reportIds;
}
