package com.rms.ors.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.rms.ors.shared.Gender;
import com.rms.ors.shared.Role;
import com.rms.ors.user.domain.User;
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
