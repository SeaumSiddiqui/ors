package com.rms.ors.controller;

import com.rms.ors.domain.Application;
import com.rms.ors.domain.User;
import com.rms.ors.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ManagementController {
    private final ApplicationService applicationService;

    // get all applications
    @GetMapping("/applications")
    public ResponseEntity<List<Application>> getAllApplications() {
        return ResponseEntity.ok(applicationService.getAll());
    }

    // get an application by id
    @GetMapping("/applications/{applicationId}") // TODO -> verify with postman && change it later to avoid clash with management getApplication
    public ResponseEntity<Application> getApplicationsById(@PathVariable Long applicationId){
        return ResponseEntity.ok(applicationService.getApplicationsById(applicationId));
    }

    // get all application create by a specific user
    @GetMapping("/applications") // TODO-> verify with postman
    public ResponseEntity<List<Application>> getAllApplicationsByUser(@RequestBody User user){
        return ResponseEntity.ok(applicationService.getAllApplicationsByUser(user));
    }

    // get an application by a user
    @GetMapping("/applications/{applicationId}") // TODO -> verify with postman && change it later to avoid clash with management getApplication
    public ResponseEntity<Application> getApplicationsByUser(@RequestBody User user, @PathVariable Long applicationId){
        return ResponseEntity.ok(applicationService.getApplicationsByIdAndUser(applicationId, user));
    }

    // update applications on status (Status.PENDING)
    @PutMapping("/applications/{applicationId}")
    public ResponseEntity<Application> updateApplications(@RequestBody Application application, @PathVariable Long applicationId) {
        return ResponseEntity.ok(applicationService.updateApplications(application, applicationId));
    }

}
