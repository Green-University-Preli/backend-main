package com.zerobin.zerobin_backend.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zerobin.zerobin_backend.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_advice")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAdvice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255)
    private String title;

    @Column(name = "advice_text", nullable = false, columnDefinition = "TEXT")
    private String adviceText; // Can be plain text or JSON

    @Column
    private Double score;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User createdBy;
}
