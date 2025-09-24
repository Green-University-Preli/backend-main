package com.zerobin.zerobin_backend.controller;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zerobin.zerobin_backend.dto.ComplaintDto;
import com.zerobin.zerobin_backend.security.JwtUtil;
import com.zerobin.zerobin_backend.service.ComplaintService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/complaints")
public class ComplaintController {
    private final ComplaintService complaintService;
    private final JwtUtil jwtUtil;

    // User/Resident submits complaint
    @PostMapping("/submit")
    public ResponseEntity<?> submitComplaint(@RequestBody ComplaintDto dto, HttpServletRequest httpRequest) {
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
        String role = jwtUtil.getRoleFromToken(token);
        if (!"USER".equalsIgnoreCase(role) && !"RESIDENT".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only users or residents can submit complaints");
        }
        String email = jwtUtil.getEmailFromToken(token);
        dto.setTimestamp(LocalDateTime.now());
        ComplaintDto saved = complaintService.saveComplaint(dto, email);
        return ResponseEntity.ok(saved);
    }

    // User/Resident views their complaints
    @GetMapping("/user")
    public ResponseEntity<?> getUserComplaints(HttpServletRequest httpRequest) {
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
        String role = jwtUtil.getRoleFromToken(token);
        if (!"USER".equalsIgnoreCase(role) && !"RESIDENT".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only users or residents can view their complaints");
        }
    String email = jwtUtil.getEmailFromToken(token);
    List<ComplaintDto> complaints = complaintService.getComplaintsByEmail(email);
        return ResponseEntity.ok(complaints);
    }

    // Admin views all complaints
    @GetMapping("/admin")
    public ResponseEntity<?> getAllComplaints(HttpServletRequest httpRequest) {
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
        String role = jwtUtil.getRoleFromToken(token);
        if (!"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only admins can view all complaints");
        }
        List<ComplaintDto> complaints = complaintService.getAllComplaints();
        return ResponseEntity.ok(complaints);
    }
}
