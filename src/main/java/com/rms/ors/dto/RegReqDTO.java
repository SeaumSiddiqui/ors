package com.rms.ors.dto;

import com.rms.ors.domain.Gender;
import com.rms.ors.domain.Role;
import lombok.Data;

@Data
public class RegReqDTO {
    private String email;
    private String password;
    private String phoneNumber;
    private String name;
    private String city;
    private Gender gender;
    private Role role;
}
