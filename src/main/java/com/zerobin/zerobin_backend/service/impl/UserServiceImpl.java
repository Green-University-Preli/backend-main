package com.zerobin.zerobin_backend.service.impl;

import com.zerobin.zerobin_backend.service.UserService;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import com.zerobin.zerobin_backend.dto.UserDto;
import com.zerobin.zerobin_backend.repository.UserRepository;
import com.zerobin.zerobin_backend.mapper.UserMapper;
import com.zerobin.zerobin_backend.entity.User;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;


@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDto register(UserDto userDto) {
        
        User user = UserMapper.toUser(userDto);

        // set default role and enabled flag
        if (user.getRole() == null) user.setRole("USER");
        user.setEnabled(true);
        // ✅ encode password
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        User savedUser = userRepository.save(user);
        return UserMapper.toUserDto(savedUser);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto updateAdminProfile(String adminEmail, UserDto update) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        // Update allowed fields
        if (update.getFirstName() != null) admin.setFirstName(update.getFirstName());
        if (update.getLastName() != null) admin.setLastName(update.getLastName());
        if (update.getPhone() != null) admin.setPhone(update.getPhone());
        if (update.getAddress() != null) admin.setAddress(update.getAddress());
        if (update.getProfileImageUrl() != null) admin.setProfileImageUrl(update.getProfileImageUrl());
        // Optional password change
        if (update.getPassword() != null && !update.getPassword().isBlank()) {
            admin.setPassword(passwordEncoder.encode(update.getPassword()));
        }

        User saved = userRepository.save(admin);
        return UserMapper.toUserDto(saved);
    }

    @Override
    public void deleteCollectorById(Long id) {
        User target = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        String role = target.getRole();
        boolean isCollector = role != null && ("COLLECTOR".equalsIgnoreCase(role) || role.toUpperCase().contains("COLLECTOR"));
        if (!isCollector) {
            throw new SecurityException("Only collectors can be deleted via this endpoint");
        }
        userRepository.deleteById(id);
    }

}
