package com.zerobin.zerobin_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zerobin.zerobin_backend.entity.notification.CollectionNotification;

public interface CollectionNotificationRepository extends JpaRepository<CollectionNotification, Long> {
    List<CollectionNotification> findByCollector_Id(Long collectorId);
    List<CollectionNotification> findByReport_ReportId(Long reportId);
}
