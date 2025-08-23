package com.zerobin.zerobin_backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.zerobin.zerobin_backend.repository.WasteReportRepository;
import com.zerobin.zerobin_backend.entity.WasteReport;
import com.zerobin.zerobin_backend.enums.WasteStatus;
import com.zerobin.zerobin_backend.repository.UserRepository;

import com.zerobin.zerobin_backend.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WasteReportService {

    private final WasteReportRepository wasteReportRepository;
    private final UserRepository userRepository;

    // report waste
    public WasteReport reportWaste(String email, String wasteDescription, String wasteType,
                                   String wasteLocation, String wasteImage) {

        // Find the user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Calculate points based on waste type
        int points = calculatePoints(wasteType);

        // Create a new waste report
        WasteReport report = WasteReport.builder()
                .user(user)
                .wasteDescription(wasteDescription)
                .wasteType(wasteType)
                .wasteLocation(wasteLocation)
                .wasteImage(wasteImage)
                .pointsEarned(points)
                .wasteStatus(WasteStatus.PENDING) // default status
                .createdAt(LocalDateTime.now())
                .build();

        // Save the report
        WasteReport savedReport = wasteReportRepository.save(report);

        // Update user's points
        user.setPoints(user.getPoints() + points);
        userRepository.save(user);

        return savedReport;
    }

    // Get all reports by user
    public List<WasteReport> getReportsByUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return wasteReportRepository.findByUser_Id(user.getId());
    }

    // Simple points calculation logic
    private int calculatePoints(String wasteType) {
        return switch (wasteType.toLowerCase()) {
            case "plastic" -> 20;
            case "organic" -> 10;
            case "metal" -> 30;
            default -> 5; // any other type
        };
    }

}
