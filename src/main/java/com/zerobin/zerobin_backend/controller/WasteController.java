package com.zerobin.zerobin_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zerobin.zerobin_backend.security.JwtUtil;
import com.zerobin.zerobin_backend.service.WasteReportService;

import jakarta.servlet.http.HttpServletRequest;

import com.zerobin.zerobin_backend.dto.WasteReportRequest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.zerobin.zerobin_backend.entity.WasteReport;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;



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

        String email = jwtUtil.getEmailFromToken(token);

        WasteReport savedReport = wasteReportService.reportWaste(
                email,
                request.getWasteDescription(),
                request.getWasteType(),
                request.getWasteLocation(),
                request.getWasteImage()
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

        String email = jwtUtil.getEmailFromToken(token);
        List<WasteReport> reports = wasteReportService.getReportsByUser(email);
        return ResponseEntity.ok(reports);
    }
    
    
    

}
