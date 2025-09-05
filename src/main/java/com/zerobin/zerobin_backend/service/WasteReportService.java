package com.zerobin.zerobin_backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    // Get all reports (admin)
    public List<WasteReport> getAllReports() {
        return wasteReportRepository.findAll();
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

    // delete report
    public void deleteReport(Long id) {
        wasteReportRepository.deleteById(id);
    }

    // Role-aware delete: ADMIN can delete any, USER can only delete own
    public boolean deleteReportAs(Long id, String requesterEmail, String role) {
        Optional<WasteReport> opt = wasteReportRepository.findById(id);
        if (opt.isEmpty()) {
            return false; // not found
        }
        WasteReport report = opt.get();

        boolean isAdmin = "ADMIN".equalsIgnoreCase(role) || "ROLE_ADMIN".equalsIgnoreCase(role)
                || (role != null && role.toUpperCase().contains("ADMIN"));
        boolean isOwner = report.getUser() != null
                && report.getUser().getEmail() != null
                && report.getUser().getEmail().equalsIgnoreCase(requesterEmail);

        if (isAdmin || ("USER".equalsIgnoreCase(role) && isOwner)) {
            wasteReportRepository.deleteById(id);
            return true;
        }

        throw new SecurityException("Not authorized to delete this report");
    }

    // Admin assigns a collector to specific reports; status becomes IN_PROGRESS
    public List<WasteReport> assignReportsToCollector(Long collectorUserId, List<Long> reportIds) {
        User collector = userRepository.findById(collectorUserId)
                .orElseThrow(() -> new RuntimeException("Collector user not found"));
        // optional role validation
        String role = collector.getRole();
        boolean isCollector = role != null && ("COLLECTOR".equalsIgnoreCase(role) || role.toUpperCase().contains("COLLECTOR"));
        if (!isCollector) {
            throw new IllegalArgumentException("Selected user is not a collector");
        }

        List<WasteReport> reports = wasteReportRepository.findByReportIdIn(reportIds);
        for (WasteReport r : reports) {
            r.setAssignedCollector(collector);
            r.setWasteStatus(WasteStatus.IN_PROGRESS);
        }
        return wasteReportRepository.saveAll(reports);
    }

    // Collector: view assigned reports
    public List<WasteReport> getAssignedReports(String collectorEmail) {
        User collector = userRepository.findByEmail(collectorEmail)
                .orElseThrow(() -> new RuntimeException("Collector not found"));
        return wasteReportRepository.findByAssignedCollector_Id(collector.getId());
    }

    // Collector completes a report only if assigned to them
    public WasteReport completeReportAsCollector(Long reportId, String collectorEmail) {
        User collector = userRepository.findByEmail(collectorEmail)
                .orElseThrow(() -> new RuntimeException("Collector not found"));
        WasteReport report = wasteReportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        if (report.getAssignedCollector() == null || !report.getAssignedCollector().getId().equals(collector.getId())) {
            throw new SecurityException("Report not assigned to this collector");
        }
        report.setWasteStatus(WasteStatus.COMPLETED);
        return wasteReportRepository.save(report);
    }
}
