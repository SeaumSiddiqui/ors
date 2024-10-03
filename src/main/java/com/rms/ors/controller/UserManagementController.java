package com.rms.ors.controller;

import com.rms.ors.domain.User;
import com.rms.ors.dto.ReqResponseDTO;
import com.rms.ors.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class UserManagementController {
    private final UserManagementService userService;

    @PostMapping("/users")
    public ResponseEntity<ReqResponseDTO> register(@RequestBody ReqResponseDTO reg){
        return ResponseEntity.ok(userService.register(reg));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ReqResponseDTO> login(@RequestBody ReqResponseDTO req){
        return ResponseEntity.ok(userService.login(req));
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<ReqResponseDTO> refreshToken(@RequestBody ReqResponseDTO ref){
        return ResponseEntity.ok(userService.refreshToken(ref));
    }

    @GetMapping("/users")
    public ResponseEntity<ReqResponseDTO> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ReqResponseDTO> getUSerByID(@PathVariable Long userId){
        return ResponseEntity.ok(userService.getUsersById(userId));
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<ReqResponseDTO> updateUser(@PathVariable Long userId, @RequestBody User updatedUser){
        return ResponseEntity.ok(userService.updateUser(userId, updatedUser));
    }

    @GetMapping("/self")
    public ResponseEntity<ReqResponseDTO> getMyProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        ReqResponseDTO response = userService.getMyInfo(email);
        return  ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<ReqResponseDTO> deleteUSer(@PathVariable Long userId){
        return ResponseEntity.ok(userService.deleteUser(userId));
    }

}
