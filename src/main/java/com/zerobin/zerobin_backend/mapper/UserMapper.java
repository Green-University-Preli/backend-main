package com.zerobin.zerobin_backend.mapper;

import com.zerobin.zerobin_backend.dto.UserDto;
import com.zerobin.zerobin_backend.entity.User;

public class UserMapper {

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPassword(),
                user.getPhone(),
                user.getAddress(),
                user.getProfileImageUrl(),
                user.getRole(),
                user.isEnabled(),
                user.getPoints()
        );
    }

    public static User toUser(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getFirstName(),
                userDto.getLastName(),
                userDto.getEmail(),
                userDto.getPassword(),
                userDto.getPhone(),
                userDto.getAddress(),
                userDto.getProfileImageUrl(),
                userDto.getRole() != null ? userDto.getRole() : "USER",
                true,
                userDto.getPoints()
        );
    }

}
