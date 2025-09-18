package com.zerobin.zerobin_backend.controller;

import com.zerobin.zerobin_backend.dto.user.UserAdviceRequest;
import com.zerobin.zerobin_backend.entity.user.UserAdvice;
import com.zerobin.zerobin_backend.security.JwtUtil;
import com.zerobin.zerobin_backend.service.UserAdviceService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/advice")
@RequiredArgsConstructor
@Slf4j
public class UserAdviceController {

    private final UserAdviceService userAdviceService;
    private final JwtUtil jwtUtil;

    private boolean isAdminRole(String role) {
        return role != null && ("ADMIN".equalsIgnoreCase(role)
                || role.toUpperCase().contains("ADMIN"));
    }

    private boolean isUserRole(String role) {
        return role != null && ("USER".equalsIgnoreCase(role)
                || role.toUpperCase().contains("USER"));
    }

    private boolean hasAccess(String role) {
        return isUserRole(role) || isAdminRole(role);
    }

    @PostMapping
    public ResponseEntity<?> createAdvice(HttpServletRequest httpRequest,
                                          @RequestBody UserAdviceRequest req) {
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
        String role = jwtUtil.getRoleFromToken(token);
        if (!isUserRole(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden: user role required");
        }
        String email = jwtUtil.getEmailFromToken(token);
        try {
            UserAdvice saved = userAdviceService.create(email, req);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception ex) {
            log.error("Failed to save user advice by {}", email, ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> listMyAdvice(HttpServletRequest httpRequest) {
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
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden: user or admin role required");
        }
        String email = jwtUtil.getEmailFromToken(token);
        List<UserAdvice> list = userAdviceService.listMine(email);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/latest")
    public ResponseEntity<?> getLatestAdvice(HttpServletRequest httpRequest) {
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
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden: user or admin role required");
        }
        String email = jwtUtil.getEmailFromToken(token);
        return userAdviceService.getLatestMine(email)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("No advice available"));
    }
}
