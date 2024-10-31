package com.rms.ors.controller;

import com.rms.ors.application.service.DashboardService;
import com.rms.ors.shared.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/dashboard")
@RequiredArgsConstructor
@RestController
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/today")
    public ResponseEntity<Map<Status, Integer>> getTodayStats() {
        return ResponseEntity.ok(dashboardService.getTodayStats());
    }


    @GetMapping("/total")
    public ResponseEntity<Map<Status, Integer>> getTotalStats() {
        return ResponseEntity.ok(dashboardService.getTotalStats());
    }


    @GetMapping("/reference")
    public ResponseEntity<Map<Status, Integer>> getUserStats(@RequestParam String name) {
        return ResponseEntity.ok(dashboardService.getUserSpecificStats(name));
    }

}
