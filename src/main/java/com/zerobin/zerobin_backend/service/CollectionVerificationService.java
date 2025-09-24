package com.zerobin.zerobin_backend.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.zerobin.zerobin_backend.dto.collector.BinCollectionVerificationResponse;
import com.zerobin.zerobin_backend.entity.Bin;
import com.zerobin.zerobin_backend.entity.CollectionVerification;
import com.zerobin.zerobin_backend.entity.User;
import com.zerobin.zerobin_backend.repository.BinRepository;
import com.zerobin.zerobin_backend.repository.CollectionVerificationRepository;
import com.zerobin.zerobin_backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CollectionVerificationService {

    private final CollectionVerificationRepository verificationRepository;
    private final BinRepository binRepository;
    private final UserRepository userRepository;

    public BinCollectionVerificationResponse verifyCollection(String collectorEmail, String binId, String note, String imageUrl, MultipartFile imageFile) {
        // Fetch user
        User collector = userRepository.findByEmail(collectorEmail)
                .orElseThrow(() -> new RuntimeException("Collector not found"));
        if (collector.getRole() == null || !collector.getRole().toUpperCase().contains("COLLECTOR")) {
            throw new SecurityException("Only collectors can submit bin collection verifications");
        }

        // Validate bin
        Bin bin = binRepository.findById(binId)
                .orElseThrow(() -> new RuntimeException("Bin not found"));

        // Image storage strategy: For now we just use provided imageUrl or a placeholder when using Multipart.
        // In a real scenario, you'd persist the file to storage (S3, local FS, etc.) and get a URL.
        String finalImageUrl = imageUrl;
        if (finalImageUrl == null || finalImageUrl.isBlank()) {
            if (imageFile != null && !imageFile.isEmpty()) {
                // Placeholder: store original filename; in future replace with actual storage path
                finalImageUrl = "uploads/" + System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            } else {
                throw new IllegalArgumentException("Image is required (either imageUrl or multipart file)");
            }
        }

        CollectionVerification saved = verificationRepository.save(CollectionVerification.builder()
                .bin(bin)
                .collector(collector)
                .note(note == null || note.isBlank() ? "Collected" : note)
                .imageUrl(finalImageUrl)
                .createdAt(LocalDateTime.now())
                .build());

        return toResponse(saved);
    }

    public Page<BinCollectionVerificationResponse> listAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return verificationRepository.findAll(pageable).map(this::toResponse);
    }

    private BinCollectionVerificationResponse toResponse(CollectionVerification v) {
        return BinCollectionVerificationResponse.builder()
                .id(v.getId())
                .binId(v.getBin().getBinId())
                .collectorId(v.getCollector().getId())
                .note(v.getNote())
                .imageUrl(v.getImageUrl())
                .createdAt(v.getCreatedAt())
                .build();
    }
}
