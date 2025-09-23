package com.zerobin.zerobin_backend.repository;

import com.zerobin.zerobin_backend.entity.Bin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BinRepository extends JpaRepository<Bin, String> {
}
