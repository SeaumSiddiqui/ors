package com.rms.ors.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class DashboardData {
    private int numOfIncomplete;
    private int numOfPending;
    private int numOfRejected;
    private int numOfAccepted;
    private int numOfGranted;
}
