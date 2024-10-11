package com.rms.ors.controller;

import com.rms.ors.user.domain.User;
import com.rms.ors.user.service.UserManagementService;
import com.rms.ors.user.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class UserManagementController {
    private final UserManagementService userService;

    @GetMapping("/users")
    public ResponseEntity<Page<UserDTO>> getAllUsers(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(userService.getAllUsers(page, size));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserDTO> getUSerByID(@PathVariable Long userId){
        return ResponseEntity.ok(userService.getUsersById(userId));
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long userId, @RequestBody User updatedUser){
        return ResponseEntity.ok(userService.updateUser(userId, updatedUser));
    }

    @GetMapping("/self")
    public ResponseEntity<UserDTO> getMyProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return  ResponseEntity.ok(userService.getMyInfo(email));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUSer(@PathVariable Long userId){
        userService.deleteUser(userId);
        return ResponseEntity.ok("User deleted");
    }

}
