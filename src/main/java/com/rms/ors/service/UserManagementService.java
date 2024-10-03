package com.rms.ors.service;

import com.rms.ors.domain.User;
import com.rms.ors.dto.ReqResponseDTO;
import com.rms.ors.repository.UserRepository;
import com.rms.ors.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserManagementService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public ReqResponseDTO register(ReqResponseDTO registrationRequest){
        ReqResponseDTO resp = new ReqResponseDTO();

        try {
            User newUser = new User();
            newUser.setEmail(registrationRequest.getEmail());
            newUser.setPhoneNumber(registrationRequest.getPhoneNumber());
            newUser.setCity(registrationRequest.getCity());
            newUser.setRole(registrationRequest.getRole());
            newUser.setGender(registrationRequest.getGender());
            newUser.setName(registrationRequest.getName());
            newUser.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            User savedUser = userRepository.save(newUser);

            if (savedUser.getId()>0) {
                resp.setUser((savedUser));
                resp.setMessage("User saved successfully");
                resp.setStatusCode(200);
            }

        }catch (Exception e){
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }


    public ReqResponseDTO login(ReqResponseDTO req) {
        ReqResponseDTO responseDTO = new ReqResponseDTO();

        try {
            authenticationManager.authenticate
                    (new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));

            var user = userRepository.findByEmail(req.getEmail()).orElseThrow();
            var jwt = jwtUtil.generateToken(user);
            var refreshToken = jwtUtil.generateRefreshToken(user);

            responseDTO.setStatusCode(200);
            responseDTO.setToken(jwt);
            responseDTO.setRole(user.getRole());
            responseDTO.setRefreshToken(refreshToken);
            responseDTO.setExpirationTime("24Hrs");
            responseDTO.setMessage("Successfully logged in");

        } catch (Exception e) {
            responseDTO.setStatusCode(500);
            responseDTO.setError(e.getMessage());
        }
        return responseDTO;
    }


    public ReqResponseDTO refreshToken(ReqResponseDTO ref) {
        ReqResponseDTO responseDTO = new ReqResponseDTO();

        try{
            String ourEmail = jwtUtil.extractUsername(ref.getToken());
            User users = userRepository.findByEmail(ourEmail).orElseThrow();

            if (jwtUtil.isTokenValid(ref.getToken(), users)) {
                var jwt = jwtUtil.generateToken(users);
                responseDTO.setStatusCode(200);
                responseDTO.setToken(jwt);
                responseDTO.setRefreshToken(ref.getToken());
                responseDTO.setExpirationTime("24Hr");
                responseDTO.setMessage("Successfully refreshed token");
            }
            responseDTO.setStatusCode(200);
            return responseDTO;

        }catch (Exception e){
            responseDTO.setStatusCode(500);
            responseDTO.setMessage(e.getMessage());
            return responseDTO;
        }
    }


    public ReqResponseDTO getAllUsers() {
        ReqResponseDTO responseDTO = new ReqResponseDTO();

        try {
            List<User> result = userRepository.findAll();

            if (!result.isEmpty()) {
                responseDTO.setUserList(result);
                responseDTO.setStatusCode(200);
                responseDTO.setMessage("Successful");
            } else {
                responseDTO.setStatusCode(404);
                responseDTO.setMessage("No users found");
            }
            return responseDTO;

        } catch (Exception e) {
            responseDTO.setStatusCode(500);
            responseDTO.setMessage("Error occurred in the server: "+ e.getMessage());
            return responseDTO;
        }
    }


    public ReqResponseDTO getUsersById(Long id) {
        ReqResponseDTO responseDTO = new ReqResponseDTO();

        try {
            User usersById = userRepository
                    .findById(id).orElseThrow(() -> new RuntimeException("User not found!"));

            responseDTO.setUser(usersById);
            responseDTO.setStatusCode(200);
            responseDTO.setMessage("User with id '" + id + "' found");

        } catch (Exception e) {
            responseDTO.setStatusCode(500);
            responseDTO.setMessage("Error occurred: " + e.getMessage());
        }
        return responseDTO;
    }


    public ReqResponseDTO updateUser(Long userId, User updatedUser) {
        ReqResponseDTO responseDTO = new ReqResponseDTO();
        try {
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isPresent()) {
                User existingUser = userOptional.get();
                existingUser.setEmail(updatedUser.getEmail());
                existingUser.setCity(updatedUser.getCity());
                existingUser.setName(updatedUser.getName());
                existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
                existingUser.setGender(updatedUser.getGender());
                existingUser.setRole(updatedUser.getRole());

                // Check if password is present in the request
                if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                    // Encode and update the password
                    existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                }

                User savedUser = userRepository.save(existingUser);
                responseDTO.setUser(savedUser);
                responseDTO.setStatusCode(200);
                responseDTO.setMessage("User updated successfully");
            } else {
                responseDTO.setStatusCode(404);
                responseDTO.setMessage("User not found for update");
            }
        } catch (Exception e) {
            responseDTO.setStatusCode(500);
            responseDTO.setMessage("Error occurred while updating user: " + e.getMessage());
        }
        return responseDTO;
    }


    public ReqResponseDTO deleteUser(Long userId) {
        ReqResponseDTO responseDTO = new ReqResponseDTO();

        try {
            Optional<User> userOptional = userRepository.findById(userId);

            if (userOptional.isPresent()) {
                userRepository.deleteById(userId);
                responseDTO.setStatusCode(200);
                responseDTO.setMessage("User deleted successfully");
            } else {
                responseDTO.setStatusCode(404);
                responseDTO.setMessage("User not found for deletion");
            }

        } catch (Exception e) {
            responseDTO.setStatusCode(500);
            responseDTO.setMessage("Error occurred while deleting an user: " + e.getMessage());
        }
        return responseDTO;
    }


    public ReqResponseDTO getMyInfo(String email) {
        ReqResponseDTO responseDTO = new ReqResponseDTO();

        try {
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isPresent()) {
                responseDTO.setUser(userOptional.get());
                responseDTO.setStatusCode(200);
                responseDTO.setMessage("successful");
            } else {
                responseDTO.setStatusCode(404);
                responseDTO.setMessage("User not found for update");
            }

        }catch (Exception e){
            responseDTO.setStatusCode(500);
            responseDTO.setMessage("Error occurred while getting user info: " + e.getMessage());
        }
        return responseDTO;
    }

}
