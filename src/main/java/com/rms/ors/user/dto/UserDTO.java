package com.rms.ors.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.rms.ors.shared.Gender;
import com.rms.ors.shared.Role;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO {
    private Long id;
    private String email;
    private String phoneNumber;
    private String name;
    private String city;
    private Gender gender;
    private Role role;
}
