package com.zerobin.zerobin_backend.controller.Admin;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zerobin.zerobin_backend.entity.User;
import com.zerobin.zerobin_backend.security.JwtUtil;
import com.zerobin.zerobin_backend.service.UserService;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.servlet.http.HttpServletRequest;

import com.zerobin.zerobin_backend.dto.UserDto;

/**
 * Controller for managing users.
 */
@RestController
@RequestMapping("/api/v1/admin/users")
@Slf4j
@RequiredArgsConstructor
public class UserManagementController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    /**
     * Checks if the given role is an admin role.
     * 
     * @param role the role to check
     * @return true if the role is an admin role, false otherwise
     */
    private boolean isAdminRole(String role) {
        return role != null && ("ADMIN".equalsIgnoreCase(role)
                || "ROLE_ADMIN".equalsIgnoreCase(role)
                || role.toUpperCase().contains("ADMIN"));
    }

    /**
     * Lists all users (user/admin/collector).
     * 
     * @param httpRequest the HTTP request
     * @return a list of users
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers(HttpServletRequest httpRequest) {
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
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Updates the admin's own profile.
     * 
     * @param httpRequest the HTTP request
     * @param update      the updated user data
     * @return the updated user data
     */
    @PutMapping("/profile")
    public ResponseEntity<?> updateAdminProfile(HttpServletRequest httpRequest, @RequestBody UserDto update) {
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
        String email = jwtUtil.getEmailFromToken(token);
        String role = jwtUtil.getRoleFromToken(token);
        if (!isAdminRole(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden: admin role required");
        }
        try {
            UserDto saved = userService.updateAdminProfile(email, update);
            return ResponseEntity.ok(saved);
        } catch (Exception ex) {
            log.error("Failed to update admin profile", ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    /**
     * Deletes a collector by ID.
     * 
     * @param httpRequest the HTTP request
     * @param id          the ID of the collector to delete
     * @return a response indicating the result of the deletion
     */
    @DeleteMapping("/collector/{id}")
    public ResponseEntity<?> deleteCollector(HttpServletRequest httpRequest, @PathVariable Long id) {
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
        try {
            userService.deleteCollectorById(id);
            return ResponseEntity.noContent().build();
        } catch (SecurityException se) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(se.getMessage());
        } catch (RuntimeException re) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(re.getMessage());
        } catch (Exception ex) {
            log.error("Failed to delete collector {}", id, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error");
        }
    }
}
