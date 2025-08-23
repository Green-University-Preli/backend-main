package com.zerobin.zerobin_backend.dto;


import lombok.Data;

@Data
public class UserUpdateRequest {

    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private String profileImageUrl;

}
