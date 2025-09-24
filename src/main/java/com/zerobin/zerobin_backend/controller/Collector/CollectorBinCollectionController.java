package com.zerobin.zerobin_backend.controller.Collector;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.zerobin.zerobin_backend.dto.collector.BinCollectionVerificationResponse;
import com.zerobin.zerobin_backend.security.JwtUtil;
import com.zerobin.zerobin_backend.service.CollectionVerificationService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CollectorBinCollectionController {

    private final JwtUtil jwtUtil;
    private final CollectionVerificationService verificationService;

    private boolean isCollector(String role) {
        return role != null && role.toUpperCase().contains("COLLECTOR");
    }

    // JSON alternative (when frontend sends application/json instead of multipart)
    @PostMapping("/collector/bin-collections/verify-json")
    public ResponseEntity<?> submitVerificationJson(HttpServletRequest request,
                                                    @RequestBody com.zerobin.zerobin_backend.dto.collector.BinCollectionVerificationRequest body) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
        String role = jwtUtil.getRoleFromToken(token);
        if (!isCollector(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only collectors can submit verifications");
        }
        if (body.getBinId() == null || body.getBinId().isBlank()) {
            return ResponseEntity.badRequest().body("binId is required");
        }
        // For JSON variant: imageUrl must be provided (no multipart file)
        if (body.getImageUrl() == null || body.getImageUrl().isBlank()) {
            return ResponseEntity.badRequest().body("imageUrl is required when not uploading a file");
        }
        String email = jwtUtil.getEmailFromToken(token);
        try {
            BinCollectionVerificationResponse resp = verificationService.verifyCollection(email, body.getBinId(), body.getNote(), body.getImageUrl(), null);
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (IllegalArgumentException iae) {
            return ResponseEntity.badRequest().body(iae.getMessage());
        } catch (SecurityException se) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(se.getMessage());
        } catch (RuntimeException re) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(re.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error");
        }
    }

    private boolean isAdmin(String role) {
        return role != null && role.toUpperCase().contains("ADMIN");
    }

    // Collector submits verification (multipart OR just imageUrl) endpoint
    @PostMapping("/collector/bin-collections/verify")
    public ResponseEntity<?> submitVerification(HttpServletRequest request,
                                                @RequestParam(required = false) String binId,
                                                @RequestParam(required = false) String note,
                                                @RequestParam(required = false) String imageUrl,
                                                @RequestParam(required = false, name = "image") MultipartFile imageFile) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
        String role = jwtUtil.getRoleFromToken(token);
        if (!isCollector(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only collectors can submit verifications");
        }
        if (binId == null || binId.isBlank()) {
            return ResponseEntity.badRequest().body("binId is required");
        }
        String email = jwtUtil.getEmailFromToken(token);
        try {
            BinCollectionVerificationResponse resp = verificationService.verifyCollection(email, binId, note, imageUrl, imageFile);
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (IllegalArgumentException iae) {
            return ResponseEntity.badRequest().body(iae.getMessage());
        } catch (SecurityException se) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(se.getMessage());
        } catch (RuntimeException re) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(re.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error");
        }
    }

    // Admin lists verifications
    @GetMapping("/admin/bin-collections")
    public ResponseEntity<?> listVerifications(HttpServletRequest request,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
        String role = jwtUtil.getRoleFromToken(token);
        if (!isAdmin(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin access required");
        }
    Page<BinCollectionVerificationResponse> pageResp = verificationService.listAll(page, size);
    com.zerobin.zerobin_backend.dto.PagedResponse<BinCollectionVerificationResponse> dto =
        com.zerobin.zerobin_backend.dto.PagedResponse.<BinCollectionVerificationResponse>builder()
            .content(pageResp.getContent())
            .page(pageResp.getNumber())
            .size(pageResp.getSize())
            .totalElements(pageResp.getTotalElements())
            .totalPages(pageResp.getTotalPages())
            .first(pageResp.isFirst())
            .last(pageResp.isLast())
            .numberOfElements(pageResp.getNumberOfElements())
            .build();
    return ResponseEntity.ok(dto);
    }
}
