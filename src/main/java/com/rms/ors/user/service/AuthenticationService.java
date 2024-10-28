package com.rms.ors.user.service;

import com.rms.ors.exception.UserAlreadyExistsException;
import com.rms.ors.user.domain.Token;
import com.rms.ors.user.domain.User;
import com.rms.ors.user.dto.LoginReqDTO;
import com.rms.ors.user.dto.RegReqDTO;
import com.rms.ors.user.dto.AuthResponseDTO;
import com.rms.ors.exception.InternalServerErrorException;
import com.rms.ors.exception.InvalidRefreshTokenException;
import com.rms.ors.exception.UserNotFoundException;
import com.rms.ors.user.repository.TokenRepository;
import com.rms.ors.user.repository.UserRepository;
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
    private final TokenRepository tokenRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthResponseDTO register(RegReqDTO reg) {
        try {
            if (userAlreadyExist(reg.getEmail())){
                throw new UserAlreadyExistsException("User: <%s> already exists".formatted(reg.getEmail()));
            }
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
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));

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
        // extract token form authorization header
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Invalid refresh token!");
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }
        String refToken = authHeader.substring(7);
        // extract the username form token
        String userEmail = jwtUtil.extractUsername(refToken);
        // find user by username
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(()-> new UserNotFoundException("User not found"));

        // check if refresh token is valid
        if (!jwtUtil.isRefreshTokenValid(refToken, user)) {
            log.warn("Invalid refresh token!");
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }
        String accessToken = jwtUtil.generateToken(user);
        saveUserToken(user, accessToken, refToken);

        return AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refToken)
                .role(user.getRole())
                .build();
    }


    private boolean userAlreadyExist(String email) {
        return userRepository.findByEmail(email).isPresent();
    }


    private AuthResponseDTO mapUserToDTO(User user) {
        String accessToken = jwtUtil.generateToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);
        // save new token before return
        saveUserToken(user, accessToken, refreshToken);

        return AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .role(user.getRole())
                .build();
    }

    private void saveUserToken(User user, String accessToken, String refreshToken) {
        Token token = Token.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenRevoked(false)
                .user(user)
                .build();
        tokenRepository.save(token);
    }

}
