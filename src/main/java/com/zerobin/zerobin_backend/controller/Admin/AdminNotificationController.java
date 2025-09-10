package com.zerobin.zerobin_backend.controller.Admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zerobin.zerobin_backend.entity.notification.CollectionNotification;
import com.zerobin.zerobin_backend.security.JwtUtil;
import com.zerobin.zerobin_backend.service.NotificationService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/notifications")
public class AdminNotificationController {

    private final NotificationService notificationService;
    private final JwtUtil jwtUtil;

    private boolean isAdminRole(String role) {
        return role != null && ("ADMIN".equalsIgnoreCase(role)
                || "ROLE_ADMIN".equalsIgnoreCase(role)
                || role.toUpperCase().contains("ADMIN"));
    }

    @GetMapping
    public ResponseEntity<?> listAll(HttpServletRequest httpRequest) {
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
        List<CollectionNotification> list = notificationService.listAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/by-collector/{collectorId}")
    public ResponseEntity<?> listByCollector(HttpServletRequest httpRequest, @PathVariable Long collectorId) {
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
        List<CollectionNotification> list = notificationService.listByCollector(collectorId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/by-report/{reportId}")
    public ResponseEntity<?> listByReport(HttpServletRequest httpRequest, @PathVariable Long reportId) {
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
        List<CollectionNotification> list = notificationService.listByReport(reportId);
        return ResponseEntity.ok(list);
    }
}
