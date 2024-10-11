package com.rms.ors.application.service;

import com.rms.ors.application.dto.DashboardData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.rms.ors.shared.Status.*;

@RequiredArgsConstructor
@Service
public class DashboardService {
    private final ApplicationService applicationService;

    public DashboardData getTodayStats() {
        LocalDateTime today = LocalDateTime.now().toLocalDate().atStartOfDay();

        int numOfIncomplete = applicationService.countByDateAndStatus(today, INCOMPLETE);
        int numOfPending = applicationService.countByDateAndStatus(today, PENDING);
        int numOfRejected = applicationService.countByDateAndStatus(today, REJECTED);
        int numOfAccepted = applicationService.countByDateAndStatus(today, ACCEPTED);
        int numOfGranted = applicationService.countByDateAndStatus(today, GRANTED);

        return new DashboardData(numOfIncomplete, numOfPending, numOfRejected, numOfAccepted, numOfGranted);
    }

    public DashboardData getTotalStats() {

        int numOfIncomplete = applicationService.countByStatus(INCOMPLETE);
        int numOfPending = applicationService.countByStatus(PENDING);
        int numOfRejected = applicationService.countByStatus(REJECTED);
        int numOfAccepted = applicationService.countByStatus(ACCEPTED);
        int numOfGranted = applicationService.countByStatus(GRANTED);

        return new DashboardData(numOfIncomplete, numOfPending, numOfRejected, numOfAccepted, numOfGranted);
    }

    public DashboardData getUserStats(Long userId) {

        int numOfIncomplete = applicationService.countByUserAndStatus(userId, INCOMPLETE);
        int numOfPending = applicationService.countByUserAndStatus(userId, PENDING);
        int numOfRejected = applicationService.countByUserAndStatus(userId ,REJECTED);
        int numOfAccepted = applicationService.countByUserAndStatus(userId ,ACCEPTED);
        int numOfGranted = applicationService.countByUserAndStatus(userId ,GRANTED);

        return new DashboardData(numOfIncomplete, numOfPending, numOfRejected, numOfAccepted, numOfGranted);
    }

}
