package com.zerobin.zerobin_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zerobin.zerobin_backend.dto.WasteReportRequest;
import com.zerobin.zerobin_backend.entity.WasteReport;
import com.zerobin.zerobin_backend.security.JwtUtil;
import com.zerobin.zerobin_backend.service.WasteReportService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;



@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/waste")
public class WasteController {

    private final WasteReportService wasteReportService;
    private final JwtUtil jwtUtil;

    @PostMapping("/report")
    public ResponseEntity<?> reportWaste(@RequestBody WasteReportRequest request, HttpServletRequest httpRequest) {
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        // Only allow regular users to create reports
        String role = jwtUtil.getRoleFromToken(token);
        if (!"USER".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only users can create waste reports");
        }

        String email = jwtUtil.getEmailFromToken(token);


    WasteReport savedReport = wasteReportService.reportWaste(
        email,
        request.getWasteDescription(),
        request.getWasteType(),
        request.getWasteLocation(),
        request.getWasteImage(),
        request.getWeight(),
        request.getBinId()
    );

        return ResponseEntity.status(HttpStatus.CREATED).body(savedReport);
    }

    //get all reports
    @GetMapping("/my-reports")
    public ResponseEntity<?> getMyReports(HttpServletRequest httpRequest) {
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        // Only allow regular users to access their own reports
        String role = jwtUtil.getRoleFromToken(token);
        if (!"USER".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only users can access their own reports");
        }

        String email = jwtUtil.getEmailFromToken(token);
        List<WasteReport> reports = wasteReportService.getReportsByUser(email);
        return ResponseEntity.ok(reports);
    }

    
    // delete report
    @DeleteMapping("/delete-report/{id}")
    public ResponseEntity<?> deleteReport(@PathVariable Long id, HttpServletRequest httpRequest) {
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        String role = jwtUtil.getRoleFromToken(token);
        String email = jwtUtil.getEmailFromToken(token);

        try {
            boolean deleted = wasteReportService.deleteReportAs(id, email, role);
            if (!deleted) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Report not found");
            }
            return ResponseEntity.ok("Report deleted successfully");
        } catch (SecurityException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
    }
    
    

}
