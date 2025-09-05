package com.zerobin.zerobin_backend.controller.Admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zerobin.zerobin_backend.entity.WasteReport;
import com.zerobin.zerobin_backend.security.JwtUtil;
import com.zerobin.zerobin_backend.service.WasteReportService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.zerobin.zerobin_backend.dto.admin.AssignCollectorRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/waste")
@Slf4j
public class AdminWasteReportController {

    private final WasteReportService wasteReportService;
    private final JwtUtil jwtUtil;

    // Admin: view all users' waste reports
    @GetMapping("/all-reports")
    public ResponseEntity<?> getAllReports(HttpServletRequest httpRequest) {
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }

        try {
            String token = authHeader.substring(7);
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
            }

            String email = jwtUtil.getEmailFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);
            log.info("Admin all-reports request by email={} role={}", email, role);

            boolean isAdmin = role != null && ("ADMIN".equalsIgnoreCase(role)
                    || "ROLE_ADMIN".equalsIgnoreCase(role)
                    || role.toUpperCase().contains("ADMIN"));
            if (!isAdmin) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Forbidden: admin role required");
            }

            List<WasteReport> all = wasteReportService.getAllReports();
            return ResponseEntity.ok(all);
        } catch (Exception ex) {
            log.error("Error in admin all-reports endpoint", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Server error: " + ex.getMessage());
        }
    }

    // Admin: assign reports to a collector
    @PostMapping("/assign")
    public ResponseEntity<?> assignReportsToCollector(HttpServletRequest httpRequest, @RequestBody AssignCollectorRequest body) {
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }

        try {
            String token = authHeader.substring(7);
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
            }

            String email = jwtUtil.getEmailFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);
            log.info("Admin assign-reports request by email={} role={}", email, role);

            boolean isAdmin = role != null && ("ADMIN".equalsIgnoreCase(role)
                    || "ROLE_ADMIN".equalsIgnoreCase(role)
                    || role.toUpperCase().contains("ADMIN"));
            if (!isAdmin) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Forbidden: admin role required");
            }

            List<WasteReport> updated = wasteReportService.assignReportsToCollector(body.getCollectorUserId(), body.getReportIds());
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException iae) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(iae.getMessage());
        } catch (Exception ex) {
            log.error("Error in admin assign-reports endpoint", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Server error: " + ex.getMessage());
        }
    }
}
