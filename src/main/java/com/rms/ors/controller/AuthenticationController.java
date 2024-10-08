package com.rms.ors.controller;

import com.rms.ors.user.dto.LoginReqDTO;
import com.rms.ors.user.dto.RegReqDTO;
import com.rms.ors.user.dto.AuthResponseDTO;
import com.rms.ors.user.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthenticationController {
    private final AuthenticationService authService;

    @PostMapping("/users")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegReqDTO request){
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginReqDTO request){
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<AuthResponseDTO> refreshToken(HttpServletRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }
}
