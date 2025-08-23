package com.zerobin.zerobin_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserProfile {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String profileImageUrl;
    private String role;
    private boolean enabled;
    private int points;

}
