package com.zerobin.zerobin_backend.controller.Collector;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.zerobin.zerobin_backend.entity.WasteReport;
import com.zerobin.zerobin_backend.security.JwtUtil;
import com.zerobin.zerobin_backend.service.WasteReportService;
import com.zerobin.zerobin_backend.service.NotificationService;
import com.zerobin.zerobin_backend.entity.notification.CollectionNotification;
import com.zerobin.zerobin_backend.dto.collector.CollectionNotificationRequest;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/collector/waste")
@Slf4j
public class CollectorWasteReportController {

    private final WasteReportService wasteReportService;
    private final JwtUtil jwtUtil;
    private final NotificationService notificationService;

    private boolean isCollectorRole(String role) {
        return role != null && ("COLLECTOR".equalsIgnoreCase(role)
                || role.toUpperCase().contains("COLLECTOR"));
    }

    // Collector: view assigned reports
    @GetMapping("/assigned")
    public ResponseEntity<?> getAssignedReports(HttpServletRequest httpRequest) {
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
        String role = jwtUtil.getRoleFromToken(token);
        if (!isCollectorRole(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden: collector role required");
        }
        String email = jwtUtil.getEmailFromToken(token);
        List<WasteReport> assigned = wasteReportService.getAssignedReports(email);
        return ResponseEntity.ok(assigned);
    }

    // Collector: complete a report (only if assigned)
    @PostMapping("/{reportId}/complete")
    public ResponseEntity<?> completeAssignedReport(HttpServletRequest httpRequest, @PathVariable Long reportId) {
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
        String role = jwtUtil.getRoleFromToken(token);
        if (!isCollectorRole(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden: collector role required");
        }
        String email = jwtUtil.getEmailFromToken(token);
        try {
            WasteReport updated = wasteReportService.completeReportAsCollector(reportId, email);
            return ResponseEntity.ok(updated);
        } catch (SecurityException se) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(se.getMessage());
        } catch (RuntimeException re) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(re.getMessage());
        } catch (Exception ex) {
            log.error("Error completing report {} by collector {}", reportId, email, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error");
        }
    }

    // Collector: send a collection notification to admin for verification
    @PostMapping("/{reportId}/notify")
    public ResponseEntity<?> notifyCollection(HttpServletRequest httpRequest,
                                              @PathVariable Long reportId,
                                              @RequestBody CollectionNotificationRequest body) {
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
        String role = jwtUtil.getRoleFromToken(token);
        if (!isCollectorRole(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden: collector role required");
        }
        String email = jwtUtil.getEmailFromToken(token);
        try {
            // Ensure the report is assigned to this collector and completed
            WasteReport report = wasteReportService.completeReportAsCollector(reportId, email);
            CollectionNotification notification = notificationService.createNotification(report, email, body.getMessage());
            return ResponseEntity.status(HttpStatus.CREATED).body(notification);
        } catch (SecurityException se) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(se.getMessage());
        } catch (RuntimeException re) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(re.getMessage());
        } catch (Exception ex) {
            log.error("Error sending notification for report {} by collector {}", reportId, email, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error");
        }
    }
}
