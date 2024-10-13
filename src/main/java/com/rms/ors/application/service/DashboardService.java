package com.rms.ors.application.service;

import com.rms.ors.application.repository.ApplicationRepository;
import com.rms.ors.shared.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class DashboardService {
    private final ApplicationRepository applicationRepository;

    public Map<Status, Integer> getTodayStats() {
        LocalDateTime today = LocalDateTime.now().toLocalDate().atStartOfDay();
        Map<Status, Integer> stats = new HashMap<>();

        for (Status status: Status.values()) {
            int count = applicationRepository.countByCreatedAtAfterAndApplicationStatus(today, status);
            stats.put(status, count);
        }
        return stats;
    }

    public Map<Status, Integer> getTotalStats() {
        Map<Status, Integer> stats = new HashMap<>();

        for (Status status: Status.values()) {
            int count = applicationRepository.countByApplicationStatus(status);
            stats.put(status, count);
        }
        return stats;
    }

    public  Map<Status, Integer> getUserSpecificStats(Long userId) {
        Map<Status, Integer> stats = new HashMap<>();

        for (Status status: Status.values()) {
            int count = applicationRepository.countBySubmittedByAndApplicationStatus(userId, status);
            stats.put(status, count);
        }
        return stats;
    }

}
