package com.rms.ors.controller;

import com.rms.ors.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/admin")
@RequiredArgsConstructor
@RestController
public class AdminController {
    private final ApplicationService applicationService;

    // delete an application
    @DeleteMapping("/application/{applicationId}")
    public ResponseEntity<String> deleteApplications(@PathVariable Long applicationId){
        applicationService.deleteApplications(applicationId);
        return ResponseEntity.ok("Application deleted");
    }
}
