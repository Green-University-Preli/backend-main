package com.zerobin.zerobin_backend.dto.admin;

public class BinCreateRequest {
    private String binId;
    private double currentFillLevelPercent;
    private int dustbinCapacityLiters;
    private double fillRatePerHour;

    public BinCreateRequest() {}

    public String getBinId() { return binId; }
    public void setBinId(String binId) { this.binId = binId; }

    public double getCurrentFillLevelPercent() { return currentFillLevelPercent; }
    public void setCurrentFillLevelPercent(double currentFillLevelPercent) { this.currentFillLevelPercent = currentFillLevelPercent; }

    public int getDustbinCapacityLiters() { return dustbinCapacityLiters; }
    public void setDustbinCapacityLiters(int dustbinCapacityLiters) { this.dustbinCapacityLiters = dustbinCapacityLiters; }

    public double getFillRatePerHour() { return fillRatePerHour; }
    public void setFillRatePerHour(double fillRatePerHour) { this.fillRatePerHour = fillRatePerHour; }
}
