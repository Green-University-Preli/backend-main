package com.zerobin.zerobin_backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.zerobin.zerobin_backend.entity.User;
import com.zerobin.zerobin_backend.entity.WasteReport;
import com.zerobin.zerobin_backend.entity.notification.CollectionNotification;
import com.zerobin.zerobin_backend.repository.CollectionNotificationRepository;
import com.zerobin.zerobin_backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final CollectionNotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public CollectionNotification createNotification(WasteReport report, String collectorEmail, String message) {
        User collector = userRepository.findByEmail(collectorEmail)
                .orElseThrow(() -> new RuntimeException("Collector not found"));
        if (report.getAssignedCollector() == null || !report.getAssignedCollector().getId().equals(collector.getId())) {
            throw new SecurityException("Report not assigned to this collector");
        }
        CollectionNotification notification = CollectionNotification.builder()
                .report(report)
                .collector(collector)
                .message(message == null ? "Collected" : message)
                .createdAt(LocalDateTime.now())
                .build();
        return notificationRepository.save(notification);
    }

    public List<CollectionNotification> listAll() {
        return notificationRepository.findAll();
    }

    public List<CollectionNotification> listByCollector(Long collectorId) {
        return notificationRepository.findByCollector_Id(collectorId);
    }

    public List<CollectionNotification> listByReport(Long reportId) {
        return notificationRepository.findByReport_ReportId(reportId);
    }
}
