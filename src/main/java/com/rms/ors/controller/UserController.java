package com.rms.ors.controller;

import com.rms.ors.application.domain.Application;
import com.rms.ors.exception.UserNotFoundException;
import com.rms.ors.user.repository.UserRepository;
import com.rms.ors.application.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RequestMapping("/user")
@RequiredArgsConstructor
@RestController
public class UserController {
    private final ApplicationService applicationService;
    private final UserRepository userRepository;

    // get all application by user(self)
    @GetMapping("/applications")
    public ResponseEntity<Page<Application>> getAllApplicationsByUser(@AuthenticationPrincipal UserDetails userDetails,
                                                                      @PathVariable(required = false) LocalDateTime startDate,
                                                                      @PathVariable(required = false) LocalDateTime endDate,
                                                                      @RequestParam(required = false) String status,
                                                                      @RequestParam(defaultValue = "1") int page,
                                                                      @RequestParam(defaultValue = "10")int size){

        return ResponseEntity.ok(applicationService.getAllApplicationsByUser(getUserId(userDetails.getUsername()), startDate, endDate, status, page, size));
    }

    // get an application by id
    @GetMapping("/applications/{applicationId}")
    public ResponseEntity<Application> getApplicationsById(@PathVariable Long applicationId){
        return ResponseEntity.ok(applicationService.getApplicationsById(applicationId));
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

    // TODO -> move it to the service method and call the service to do this task
    private Long getUserId(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(()-> new UserNotFoundException("User <%s> not found".formatted(username))).getId();
    }
}
