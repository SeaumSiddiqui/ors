package com.rms.ors.controller;

import com.rms.ors.domain.Application;
import com.rms.ors.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class UserController {
    private final ApplicationService applicationService;

    // get all application create by a specific user (self)
    @GetMapping("/applications")
    public ResponseEntity<List<Application>> getAllApplicationsByUserId(@AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(applicationService.getAllApplicationsByUserId(userDetails.getUsername()));
    }

    // get an application created by a user (self)
    @GetMapping("/applications/{applicationId}") // TODO -> change it later to avoid clash with management getApplication
    public ResponseEntity<Application> getApplicationByIdAndUserId(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long applicationId){
        return ResponseEntity.ok(applicationService.getAllApplicationsByIdAndUserId(userDetails.getUsername(), applicationId));
    }

    // create an application
    @PostMapping("/applications")
    public ResponseEntity<Application> createApplication(@RequestBody Application application) {
        return ResponseEntity.ok(applicationService.createApplication(application));
    }

    // update self created application on (status: incomplete/rejected)
    @PutMapping("/applications/{applicationId}")
    public ResponseEntity<Application> updateApplication(@RequestBody Application application, @PathVariable Long applicationId) {
        return ResponseEntity.ok(applicationService.updateApplication(application, applicationId));
    }
}
