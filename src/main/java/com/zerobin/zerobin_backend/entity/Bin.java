package com.zerobin.zerobin_backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "bins")
public class Bin {
    @Id
    private String binId;
    private double currentFillLevelPercent;
    private int dustbinCapacityLiters;
    private double fillRatePerHour;

    public Bin() {}

    public Bin(String binId, double currentFillLevelPercent, int dustbinCapacityLiters, double fillRatePerHour) {
        this.binId = binId;
        this.currentFillLevelPercent = currentFillLevelPercent;
        this.dustbinCapacityLiters = dustbinCapacityLiters;
        this.fillRatePerHour = fillRatePerHour;
    }

    public String getBinId() { return binId; }
    public void setBinId(String binId) { this.binId = binId; }

    public double getCurrentFillLevelPercent() { return currentFillLevelPercent; }
    public void setCurrentFillLevelPercent(double currentFillLevelPercent) { this.currentFillLevelPercent = currentFillLevelPercent; }

    public int getDustbinCapacityLiters() { return dustbinCapacityLiters; }
    public void setDustbinCapacityLiters(int dustbinCapacityLiters) { this.dustbinCapacityLiters = dustbinCapacityLiters; }

    public double getFillRatePerHour() { return fillRatePerHour; }
    public void setFillRatePerHour(double fillRatePerHour) { this.fillRatePerHour = fillRatePerHour; }
}
