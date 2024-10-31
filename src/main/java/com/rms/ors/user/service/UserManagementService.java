package com.rms.ors.user.service;

import com.rms.ors.exception.UserAlreadyExistsException;
import com.rms.ors.user.domain.User;
import com.rms.ors.exception.UserNotFoundException;
import com.rms.ors.user.repository.UserRepository;
import com.rms.ors.user.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserManagementService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<UserDTO> getAllUsers(int page, int size) {
        try {
            return userRepository.findAll(PageRequest.of(page, size, Sort.by("name"))).map(this::mapUserToDTO);

        } catch (Exception e) {
            log.error("Error retrieving users", e);
            throw new RuntimeException("Failed to retrieve users", e);
        }
    }


    public UserDTO getUsersById(Long userId) {
        return mapUserToDTO(userRepository.findById(userId)
                .orElseThrow(()-> {
            log.warn("User with Id {} not found", userId);
            return new UserNotFoundException("User with Id <%d> not found".formatted(userId));
        }));
    }


    public UserDTO updateUser(Long userId, User updatedUser) {
        userRepository.findByEmail(updatedUser.getEmail()).ifPresent(existingUser-> {
            if (!existingUser.getId().equals(userId)) {
                log.warn("Email {} is already in use by the use with Id {}", updatedUser.getEmail(), existingUser.getId());
                throw new UserAlreadyExistsException("Email <%s> is already in use".formatted(updatedUser.getEmail()));
            }
        });

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User with Id {} not found for update", userId);
                    return new UserNotFoundException("User with Id <%d> not found to update".formatted(userId));
                });

        user.setEmail(updatedUser.getEmail());
        user.setPhoneNumber(updatedUser.getPhoneNumber());
        user.setName(updatedUser.getName());
        user.setCity(updatedUser.getCity());
        user.setGender(updatedUser.getGender());
        user.setRole(updatedUser.getRole());

        // Check if password has changed
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        User savedUser = userRepository.save(user);
        log.info("User with Id {} successfully updated", userId);

        return mapUserToDTO(savedUser);
    }


    public void deleteUser(Long userId) {
        try {
            userRepository.deleteById(userId);
            log.info("User with Id {} successfully deleted", userId);
        } catch (EmptyResultDataAccessException ex) {
            log.warn("User with Id {} not found for deletion", userId);
            throw new UserNotFoundException("User with Id <%d> not found for deletion".formatted(userId));
        }
    }


    public UserDTO getMyInfo(String username) {
        return mapUserToDTO(userRepository.findByEmail(username)
                .orElseThrow(()-> {
                    log.warn("User {} not found", username);
                    return new UserNotFoundException("User <%s> not found".formatted(username));
                }));
    }


    private UserDTO mapUserToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .name(user.getName())
                .city(user.getCity())
                .gender(user.getGender())
                .role(user.getRole())
                .build();
    }

    // Method for dashboard service
    public Long getUserIdByName(String name) {
        return Optional.ofNullable(userRepository.findFirstByNameIgnoreCase(name))
                .orElseThrow(()-> new UserNotFoundException("User with Id <%s> not found for deletion".formatted(name)));
    }

}
