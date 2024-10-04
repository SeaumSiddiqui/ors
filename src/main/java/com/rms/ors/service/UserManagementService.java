package com.rms.ors.service;

import com.rms.ors.domain.User;
import com.rms.ors.dto.*;
import com.rms.ors.exception.UserNotFoundException;
import com.rms.ors.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserManagementService {
    private static final Logger log = LoggerFactory.getLogger(UserManagementService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserDTO> getAllUsers() {
        try {
            List<User> userList = userRepository.findAll();

            return userList.stream()
                    .map(this::mapUserToDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error retrieving users", e);
            throw new RuntimeException("Failed to retrieve users", e); // Example
        }
    }


    public UserDTO getUsersById(Long userId) {

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            log.warn("User with Id {} not found", userId);
            throw new UserNotFoundException("User with Id %d not found".formatted(userId));
        }
        return mapUserToDTO(optionalUser.get());
    }


    public UserDTO updateUser(Long userId, User updatedUser) {
        Optional<User> existingUser = userRepository.findById(userId);

        if (existingUser.isEmpty()) {
            log.warn("User with Id {} not found to update", userId);
            throw new UserNotFoundException("User with Id %d not found to update".formatted(userId));
        }

        User user = existingUser.get();
        user.setEmail(updatedUser.getEmail());
        user.setPhoneNumber(updatedUser.getPhoneNumber());
        user.setName(updatedUser.getName());
        user.setCity(updatedUser.getCity());
        user.setGender(updatedUser.getGender());
        user.setRole(updatedUser.getRole());
        // check if password has changed
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        User savedUser = userRepository.save(user);
        log.info("User with Id {} successfully updated", userId);
        return mapUserToDTO(savedUser);
    }


    public void deleteUser(Long userId) {

        if (userRepository.existsById(userId)) {
            throw new UserNotFoundException("User %d not found".formatted(userId));
        }
        userRepository.deleteById(userId);
    }


    public UserDTO getMyInfo(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            return mapUserToDTO(optionalUser.get());
        }

        log.warn("User {} not found", email);
        throw new UserNotFoundException("User with Id %s not found".formatted(email));
    }


    private UserDTO mapUserToDTO(User user) {
        return UserDTO.builder()
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .name(user.getName())
                .city(user.getCity())
                .gender(user.getGender())
                .role(user.getRole())
                .build();
    }

}
