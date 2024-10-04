package com.rms.ors.service;

import com.rms.ors.domain.User;
import com.rms.ors.dto.LoginReqDTO;
import com.rms.ors.dto.RegReqDTO;
import com.rms.ors.dto.AuthResponseDTO;
import com.rms.ors.exception.InternalServerErrorException;
import com.rms.ors.exception.InvalidRefreshTokenException;
import com.rms.ors.exception.UserNotFoundException;
import com.rms.ors.repository.UserRepository;
import com.rms.ors.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthenticationService {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthResponseDTO register(RegReqDTO reg) {
        try {
            User newUser = User.builder()
                    .email(reg.getEmail())
                    .phoneNumber(reg.getPhoneNumber())
                    .name(reg.getName())
                    .city(reg.getCity())
                    .gender(reg.getGender())
                    .role(reg.getRole())
                    .password(passwordEncoder.encode(reg.getPassword()))
                    .build();
            User savedUser = userRepository.save(newUser);
            return mapUserToDTO(savedUser);

        } catch (Exception ex) {
            log.error("Error during user registration: {}", ex.getMessage(), ex);
            throw new InternalServerErrorException("An unexpected error occurred");
        }
    }


    public AuthResponseDTO login(LoginReqDTO req) {
        try {
            authenticationManager.authenticate
                    (new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));

            var user = userRepository.findByEmail(req.getEmail())
                    .orElseThrow(()-> new UsernameNotFoundException("User not found"));

            log.info("User {} logged successfully", user.getEmail());
            return mapUserToDTO(user);

        } catch (AuthenticationException ex) {
            log.error("User authentication failed: {}", ex.getMessage());
            throw new BadCredentialsException("Invalid credential");
        } catch (Exception ex) {
            log.error("Error during login: {}", ex.getMessage(), ex);
            throw new InternalServerErrorException("An unexpected error occurred");
        }
    }


    public AuthResponseDTO refreshToken(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Invalid refresh token!");
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }

        String refToken = authHeader.substring(7);
        String userEmail = jwtUtil.extractUsername(refToken);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(()-> new UserNotFoundException("User not found"));

        if (!jwtUtil.isTokenValid(refToken, user)) {
            log.warn("Invalid refresh token!");
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }
        return mapUserToDTO(user);
    }

//    public AuthResponseDTO refreshToken(String request) {
//        String userEmail = jwtUtil.extractUsername(request);
//
//        try {
//            User user = userRepository.findByEmail(userEmail) // get user
//                    .orElseThrow(() -> new UserNotFoundException("User not found"));
//
//            if (jwtUtil.isTokenValid(request, user)) { // check token validity
//                log.info("Token refresh successful for user: {}", userEmail);
//                return mapUserToDTO(user);
//            } else {
//                //log.warn("Invalid refresh token!");
//                throw new InvalidRefreshTokenException("Invalid refresh token");
//            }
//
//        } catch (Exception ex) {
//            log.error("Error refreshing token: {}", ex.getMessage());
//            throw new InvalidRefreshTokenException("An unexpected error occurred");
//        }
//    }

    
    private AuthResponseDTO mapUserToDTO(User user) {
        return AuthResponseDTO.builder()
                .accessToken(jwtUtil.generateToken(user))
                .refreshToken(jwtUtil.generateRefreshToken(user))
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
