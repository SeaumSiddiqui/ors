package com.rms.ors.controller;

import com.rms.ors.domain.Application;
import com.rms.ors.repository.UserRepository;
import com.rms.ors.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/management")
@RequiredArgsConstructor
@RestController
public class ManagementController {
    private final ApplicationService applicationService;

    @GetMapping("/applications/status/{status}")
    public ResponseEntity<List<Application>> getApplicationsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(applicationService.getApplicationsByStatus(status));
    }

    // get all applications
    @GetMapping("/applications")
    public ResponseEntity<List<Application>> getAllApplications() {
        return ResponseEntity.ok(applicationService.getAllApplications());
    }

    // get an application by id
    @GetMapping("/applications/id/{applicationId}")
    public ResponseEntity<Application> getApplicationsById(@PathVariable Long applicationId){
        return ResponseEntity.ok(applicationService.getApplicationsById(applicationId));
    }

    // get all application by user
    @GetMapping("/applications/user/{userId}")
    public ResponseEntity<List<Application>> getAllApplicationsByUser(@PathVariable Long userId){
        return ResponseEntity.ok(applicationService.getAllApplicationsByUser(userId));
    }

    // get an application by a user
    @GetMapping("/applications/user/{userId}/id/{applicationId}")
    public ResponseEntity<Application> getApplicationsByUser(@PathVariable Long userId, @PathVariable Long applicationId){
        return ResponseEntity.ok(applicationService.getApplicationsByIdAndUser(applicationId,userId));
    }

    // update applications on status (Status.PENDING)
    @PutMapping("/applications/{applicationId}")
    public ResponseEntity<Application> updateApplications(@RequestBody Application application, @PathVariable Long applicationId) {
        return ResponseEntity.ok(applicationService.updateApplications(application, applicationId));
    }

}
