package com.zerobin.zerobin_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Sort;

import com.zerobin.zerobin_backend.entity.summary.WasteSummary;

import java.util.List;

public interface WasteSummaryRepository extends JpaRepository<WasteSummary, Long> {

    @Query("select w from WasteSummary w order by w.createdAt desc")
    List<WasteSummary> findAllOrderByCreatedAtDesc();

    default List<WasteSummary> findAllSorted() {
        return findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }
}
