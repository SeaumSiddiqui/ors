package com.rms.ors.user.dto;

import lombok.Data;

@Data
public class LoginReqDTO {
    private String email;
    private String password;
}
