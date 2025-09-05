package com.zerobin.zerobin_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zerobin.zerobin_backend.entity.WasteReport;

public interface WasteReportRepository extends JpaRepository<WasteReport, Long> {

    List<WasteReport> findByUser_Id(Long userId);
    
    // delete report
    void deleteById(Long id);

    // assignment related
    List<WasteReport> findByAssignedCollector_Id(Long collectorId);

    List<WasteReport> findByReportIdIn(List<Long> reportIds);
}
