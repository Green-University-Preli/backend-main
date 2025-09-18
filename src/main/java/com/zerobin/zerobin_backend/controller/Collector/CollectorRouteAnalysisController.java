package com.zerobin.zerobin_backend.controller.Collector;

import com.zerobin.zerobin_backend.dto.collector.CollectorRouteAnalysisRequest;
import com.zerobin.zerobin_backend.entity.collector.CollectorRouteAnalysis;
import com.zerobin.zerobin_backend.security.JwtUtil;
import com.zerobin.zerobin_backend.service.CollectorRouteAnalysisService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/collector/route-analyses")
@RequiredArgsConstructor
@Slf4j
public class CollectorRouteAnalysisController {

    private final CollectorRouteAnalysisService analysisService;
    private final JwtUtil jwtUtil;

    private boolean isCollectorRole(String role) {
        return role != null && ("COLLECTOR".equalsIgnoreCase(role)
                || role.toUpperCase().contains("COLLECTOR"));
    }

    private boolean hasAccess(String role) {
        // In case admins also want to query, allow admin as well
        return role != null && (isCollectorRole(role) || role.toUpperCase().contains("ADMIN"));
    }

    @PostMapping
    public ResponseEntity<?> create(HttpServletRequest httpRequest,
                                    @RequestBody CollectorRouteAnalysisRequest req) {
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
            CollectorRouteAnalysis saved = analysisService.create(email, req);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception ex) {
            log.error("Failed to save route analysis by {}", email, ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> listMine(HttpServletRequest httpRequest) {
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
        String role = jwtUtil.getRoleFromToken(token);
        if (!hasAccess(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden: collector or admin role required");
        }
        String email = jwtUtil.getEmailFromToken(token);
        List<CollectorRouteAnalysis> list = analysisService.listMine(email);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/latest")
    public ResponseEntity<?> latestMine(HttpServletRequest httpRequest) {
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
        String role = jwtUtil.getRoleFromToken(token);
        if (!hasAccess(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden: collector or admin role required");
        }
        String email = jwtUtil.getEmailFromToken(token);
        return analysisService.getLatestMine(email)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("No analyses available"));
    }
}
