package com.zerobin.zerobin_backend.dto;

import java.time.LocalDateTime;

public class ComplaintDto {
    private Long complaintId;
    private Long userId;
    private String text;
    private String location;
    private String sentiment;
    private Double confidence;
    private String reasoning;
    private LocalDateTime timestamp;

    // Getters and setters
    public Long getComplaintId() { return complaintId; }
    public void setComplaintId(Long complaintId) { this.complaintId = complaintId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getSentiment() { return sentiment; }
    public void setSentiment(String sentiment) { this.sentiment = sentiment; }
    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
    public String getReasoning() { return reasoning; }
    public void setReasoning(String reasoning) { this.reasoning = reasoning; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
