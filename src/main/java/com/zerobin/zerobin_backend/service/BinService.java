package com.zerobin.zerobin_backend.service;

import com.zerobin.zerobin_backend.dto.admin.BinCreateRequest;
import com.zerobin.zerobin_backend.entity.Bin;
import com.zerobin.zerobin_backend.repository.BinRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BinService {
    private final BinRepository binRepository;

    @Autowired
    public BinService(BinRepository binRepository) {
        this.binRepository = binRepository;
    }

    public Bin createBin(BinCreateRequest request) {
        Bin bin = new Bin(
            request.getBinId(),
            request.getCurrentFillLevelPercent(),
            request.getDustbinCapacityLiters(),
            request.getFillRatePerHour()
        );
        return binRepository.save(bin);
    }

    public List<Bin> getAllBins() {
        return binRepository.findAll();
    }

    public Bin editBin(String binId, BinCreateRequest request) {
        Optional<Bin> optionalBin = binRepository.findById(binId);
        if (optionalBin.isEmpty()) {
            throw new RuntimeException("Bin not found");
        }
        Bin bin = optionalBin.get();
        bin.setCurrentFillLevelPercent(request.getCurrentFillLevelPercent());
        bin.setDustbinCapacityLiters(request.getDustbinCapacityLiters());
        bin.setFillRatePerHour(request.getFillRatePerHour());
        return binRepository.save(bin);
    }

    public void deleteBin(String binId) {
        if (!binRepository.existsById(binId)) {
            throw new RuntimeException("Bin not found");
        }
        binRepository.deleteById(binId);
    }
}
