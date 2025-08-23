package com.zerobin.zerobin_backend.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zerobin.zerobin_backend.dto.UserDto;
import com.zerobin.zerobin_backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody UserDto userDto) {
        UserDto savedUser = userService.register(userDto);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    

}
