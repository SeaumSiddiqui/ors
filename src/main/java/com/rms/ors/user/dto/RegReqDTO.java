package com.rms.ors.user.dto;

import com.rms.ors.shared.Gender;
import com.rms.ors.shared.Role;
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
