package com.zerobin.zerobin_backend.service;

import com.zerobin.zerobin_backend.dto.UserDto;
import java.util.List;


public interface UserService {

    UserDto register(UserDto userDto);

    // Admin: list all users (users, admins, collectors)
    List<UserDto> getAllUsers();

    // Admin: update own profile using email from token
    UserDto updateAdminProfile(String adminEmail, UserDto update);

    // Admin: delete a collector by id
    void deleteCollectorById(Long id);
}
