package com.zerobin.zerobin_backend.controller.Admin;

import com.zerobin.zerobin_backend.dto.admin.BinCreateRequest;
import com.zerobin.zerobin_backend.entity.Bin;
import com.zerobin.zerobin_backend.service.BinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;
import com.zerobin.zerobin_backend.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/admin/bin")
public class AdminBinController {
    private final BinService binService;
    private final JwtUtil jwtUtil;

    @Autowired
    public AdminBinController(BinService binService, JwtUtil jwtUtil) {
        this.binService = binService;
        this.jwtUtil = jwtUtil;
    }

    private boolean isAdminRole(String role) {
        return role != null && ("ADMIN".equalsIgnoreCase(role)
                || "ROLE_ADMIN".equalsIgnoreCase(role)
                || role.toUpperCase().contains("ADMIN"));
    }

    @PostMapping
    public ResponseEntity<?> addBin(HttpServletRequest httpRequest, @RequestBody BinCreateRequest request) {
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
        Bin bin = binService.createBin(request);
        return ResponseEntity.ok(bin);
    }

    @GetMapping
    public ResponseEntity<?> getAllBins(HttpServletRequest httpRequest) {
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
        List<Bin> bins = binService.getAllBins();
        return ResponseEntity.ok(bins);
    }

    @PutMapping("/{binId}")
    public ResponseEntity<?> editBin(HttpServletRequest httpRequest, @PathVariable String binId, @RequestBody BinCreateRequest request) {
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
        Bin updatedBin = binService.editBin(binId, request);
        return ResponseEntity.ok(updatedBin);
    }

    @DeleteMapping("/{binId}")
    public ResponseEntity<?> deleteBin(HttpServletRequest httpRequest, @PathVariable String binId) {
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
        binService.deleteBin(binId);
        return ResponseEntity.noContent().build();
    }
}
