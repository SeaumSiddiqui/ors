package com.rms.ors.controller;

import com.rms.ors.domain.Application;
import com.rms.ors.domain.User;
import com.rms.ors.exception.UserNotFoundException;
import com.rms.ors.repository.UserRepository;
import com.rms.ors.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/user")
@RequiredArgsConstructor
@RestController
public class UserController {
    private final ApplicationService applicationService;
    private final UserRepository userRepository;

    // get all application by user (self)
    @GetMapping("/applications")
    public ResponseEntity<List<Application>> getAllApplicationsByUser(@AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(applicationService.getAllApplicationsByUser(getUser(userDetails.getUsername())));
    }

    // get an application by user (self)
    @GetMapping("/applications/{applicationId}")
    public ResponseEntity<Application> getApplicationsByIdAndUser(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long applicationId){
        return ResponseEntity.ok(applicationService.getApplicationsByIdAndUser(applicationId, getUser(userDetails.getUsername())));
    }

    // create an application
    @PostMapping("/applications")
    public ResponseEntity<Application> createApplications(@RequestBody Application application) {
        return ResponseEntity.ok(applicationService.createApplications(application));
    }

    // update self created application on (status: incomplete/rejected)
    @PutMapping("/applications/{applicationId}")
    public ResponseEntity<Application> updateApplications(@RequestBody Application application, @PathVariable Long applicationId) {
        return ResponseEntity.ok(applicationService.updateApplications(application, applicationId));
    }


    private User getUser(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(()-> new UserNotFoundException("User <%s> not found".formatted(username)));
    }
}
