package com.zerobin.zerobin_backend.entity;

import java.time.LocalDateTime;

import com.zerobin.zerobin_backend.enums.WasteStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Column;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "waste_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WasteReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "report_id" , nullable = false)
    private Long reportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id" , nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User user;

    @Column(name = "waste_description" , nullable = false)
    private String wasteDescription;

    @Column(name = "waste_type" , nullable = false)
    private String wasteType;

    @Column(name = "waste_location" , nullable = false)
    private String wasteLocation;

    @Column(name = "waste_image" , nullable = false)
    private String wasteImage;

    @Enumerated(EnumType.STRING)
    @Column(name = "waste_status" , nullable = false)
    @Builder.Default
    private WasteStatus wasteStatus = WasteStatus.PENDING;

    @Column(name = "points_earned" , nullable = false)
    private int pointsEarned;

    @Column(name = "created_at" , nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_collector_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User assignedCollector;

}
