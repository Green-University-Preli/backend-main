package com.zerobin.zerobin_backend.controller.Admin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zerobin.zerobin_backend.dto.admin.WasteSummaryRequest;
import com.zerobin.zerobin_backend.entity.summary.WasteSummary;
import com.zerobin.zerobin_backend.security.JwtUtil;
import com.zerobin.zerobin_backend.service.WasteSummaryService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/waste-summaries")
@RequiredArgsConstructor
@Slf4j
public class WasteSummaryController {

    private final WasteSummaryService wasteSummaryService;
    private final JwtUtil jwtUtil;

    private boolean isAdminRole(String role) {
        return role != null && ("ADMIN".equalsIgnoreCase(role)
                || "ROLE_ADMIN".equalsIgnoreCase(role)
                || role.toUpperCase().contains("ADMIN"));
    }

    @PostMapping
    public ResponseEntity<?> createSummary(HttpServletRequest httpRequest, @RequestBody WasteSummaryRequest req) {
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
        String role = jwtUtil.getRoleFromToken(token);
        if (!isAdminRole(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden: admin role required");
        }
        String email = jwtUtil.getEmailFromToken(token);
        try {
            WasteSummary saved = wasteSummaryService.createSummary(email, req);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception ex) {
            log.error("Failed to create waste summary by {}", email, ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> listSummaries(HttpServletRequest httpRequest) {
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
        String role = jwtUtil.getRoleFromToken(token);
        if (!isAdminRole(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden: admin role required");
        }
        List<WasteSummary> list = wasteSummaryService.listAll();
        return ResponseEntity.ok(list);
    }
}
