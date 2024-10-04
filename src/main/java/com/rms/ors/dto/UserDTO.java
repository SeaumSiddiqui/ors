package com.rms.ors.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.rms.ors.domain.Gender;
import com.rms.ors.domain.Role;
import com.rms.ors.domain.User;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO {
    private String email;
    private String phoneNumber;
    private String name;
    private String city;
    private Gender gender;
    private Role role;
    private User user;
    private List<User> userList;
}
