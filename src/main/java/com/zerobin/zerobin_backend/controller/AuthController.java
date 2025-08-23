package com.zerobin.zerobin_backend.controller;

import com.zerobin.zerobin_backend.dto.LoginRequest;
import com.zerobin.zerobin_backend.dto.LoginResponse;
import com.zerobin.zerobin_backend.dto.UserProfile;
import com.zerobin.zerobin_backend.entity.User;
import com.zerobin.zerobin_backend.repository.UserRepository;
import com.zerobin.zerobin_backend.security.JwtUtil;
import com.zerobin.zerobin_backend.dto.UserUpdateRequest;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;




@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

        // Find user by email
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }

        // Check password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }

        // ✅ Generate JWT with email + role
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        // ✅ Return token + role (optional)
        return ResponseEntity.ok(new LoginResponse(token, user.getRole()));
    }


    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7); // remove "Bearer "
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        String email = jwtUtil.getEmailFromToken(token);
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        UserProfile profile = new UserProfile(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getPhone(),
            user.getAddress(),
            user.getProfileImageUrl(),
            user.getRole(),
            user.isEnabled(),
            user.getPoints()
        );

        return ResponseEntity.ok(profile);
    }


    @PutMapping("/updateProfile")
    public ResponseEntity<?> updateProfile(@RequestBody UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7); // remove "Bearer "
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        String email = jwtUtil.getEmailFromToken(token);
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        if (userUpdateRequest.getFirstName() != null) user.setFirstName(userUpdateRequest.getFirstName());
        if (userUpdateRequest.getLastName() != null) user.setLastName(userUpdateRequest.getLastName());
        if (userUpdateRequest.getPhone() != null) user.setPhone(userUpdateRequest.getPhone());
        if (userUpdateRequest.getAddress() != null) user.setAddress(userUpdateRequest.getAddress());
        if (userUpdateRequest.getProfileImageUrl() != null) user.setProfileImageUrl(userUpdateRequest.getProfileImageUrl());

        userRepository.save(user);

        return ResponseEntity.ok("Profile updated successfully");
    }
    
}
