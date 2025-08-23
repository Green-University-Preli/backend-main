package com.zerobin.zerobin_backend.dto;

import lombok.Data;

@Data
public class WasteReportRequest {

    private String wasteDescription;
    private String wasteType;
    private String wasteLocation;
    private String wasteImage;
}
