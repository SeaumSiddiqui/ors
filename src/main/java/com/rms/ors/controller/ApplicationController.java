package com.rms.ors.controller;

import com.rms.ors.application.domain.Application;
import com.rms.ors.application.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RequestMapping("/applications")
@RequiredArgsConstructor
@RestController
public class ApplicationController {
    private final ApplicationService applicationService;

    // get all applications
    @GetMapping("/search")
    public ResponseEntity<Page<Application>> getAllApplications(@RequestParam(required = false) Long submittedBy,
                                                                @RequestParam(required = false) Long reviewedBy,
                                                                @RequestParam(required = false) LocalDateTime startedDate,
                                                                @RequestParam(required = false) LocalDateTime endDate,
                                                                @RequestParam(required = false) String fullName,
                                                                @RequestParam(required = false) String fathersName,
                                                                @RequestParam(required = false) String applicationStatus,
                                                                @RequestParam(required = false, defaultValue = "createdAt") String sortField,
                                                                @RequestParam(required = false, defaultValue = "DESC") String sortDirection,
                                                                @RequestParam(defaultValue = "0")int page,
                                                                @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(applicationService.getAllApplications
                (submittedBy, reviewedBy, startedDate, endDate, fullName, fathersName, applicationStatus, sortField, sortDirection, page, size));
    }

    // get application by id
    @GetMapping("/{applicationId}")
    public ResponseEntity<Application> getApplicationsById(@PathVariable Long applicationId){
        return ResponseEntity.ok(applicationService.getApplicationsById(applicationId));
    }

    // create application
    @PostMapping("/create")
    public ResponseEntity<Application> createApplications(@RequestBody Application application) {
        return ResponseEntity.ok(applicationService.createApplications(application));
    }

    // update application
    @PutMapping("/{applicationId}")
    public ResponseEntity<Application> updateApplications(@RequestBody Application application, @PathVariable Long applicationId) {
        return ResponseEntity.ok(applicationService.updateApplications(application, applicationId));
    }

    // delete an application by id
    @DeleteMapping("/{applicationId}")
    public ResponseEntity<String> deleteApplications(@PathVariable Long applicationId){
        applicationService.deleteApplications(applicationId);
        return ResponseEntity.ok("Application deleted");
    }

}
