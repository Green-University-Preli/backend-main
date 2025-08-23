package com.zerobin.zerobin_backend.service.impl;

import com.zerobin.zerobin_backend.service.UserService;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import com.zerobin.zerobin_backend.dto.UserDto;
import com.zerobin.zerobin_backend.repository.UserRepository;
import com.zerobin.zerobin_backend.mapper.UserMapper;
import com.zerobin.zerobin_backend.entity.User;
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

}
