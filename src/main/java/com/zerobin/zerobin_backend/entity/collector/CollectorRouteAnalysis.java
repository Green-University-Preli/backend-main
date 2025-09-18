package com.zerobin.zerobin_backend.entity.collector;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zerobin.zerobin_backend.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "collector_route_analyses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectorRouteAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255)
    private String title;

    @Column(name = "analysis_data", nullable = false, columnDefinition = "TEXT")
    private String analysisData; // JSON string representing AI suggestion/details

    @Column
    private Double priorityScore;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User createdBy;
}
