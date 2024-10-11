package com.rms.ors.controller;

import com.rms.ors.application.domain.Application;
import com.rms.ors.application.dto.DashboardData;
import com.rms.ors.application.service.ApplicationService;
import com.rms.ors.application.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RequestMapping("/dashboard")
@RequiredArgsConstructor
@RestController
public class DashboardController {
    private final ApplicationService applicationService;
    private final DashboardService dashboardService;


    @GetMapping("/applications")
    public ResponseEntity<Page<Application>> getAllApplications(@RequestParam(required = false) Long userId,
                                                                      @RequestParam(required = false) LocalDateTime startDate,
                                                                      @RequestParam(required = false) LocalDateTime endDate,
                                                                      @RequestParam String status,
                                                                      @RequestParam(defaultValue = "1") int page,
                                                                      @RequestParam(defaultValue = "10") int size){

        return ResponseEntity.ok(applicationService.getAllApplicationsByUser(userId, startDate, endDate, status, page, size));
    }


    @GetMapping("/today")
    public ResponseEntity<DashboardData> getTodayStats() {
        return ResponseEntity.ok(dashboardService.getTodayStats());
    }


    @GetMapping("/total")
    public ResponseEntity<DashboardData> getTotalStats() {
        return ResponseEntity.ok(dashboardService.getTotalStats());
    }


    @GetMapping("/reference/{userId}")
    public ResponseEntity<DashboardData> getUserStats(@PathVariable Long userId) {
        return ResponseEntity.ok(dashboardService.getUserStats(userId));
    }















}
