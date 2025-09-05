package com.zerobin.zerobin_backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    @JsonProperty(access = Access.WRITE_ONLY)
    private String password;
    private String phone;
    private String address;
    private String profileImageUrl;
    private String role;
    private boolean enabled;
    private int points;

}
