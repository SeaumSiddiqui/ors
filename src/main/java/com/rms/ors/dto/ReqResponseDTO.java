package com.rms.ors.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.rms.ors.domain.Gender;
import com.rms.ors.domain.Role;
import com.rms.ors.domain.User;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReqResponseDTO {
    private int statusCode;
    private String error;
    private String message;
    private String token;
    private String refreshToken;
    private String expirationTime;

    private String email;
    private String name;
    private String password;
    private String city;
    private String phoneNumber;
    private Gender gender;
    private Role role;

    private User user;
    private List<User> userList;
}
