package com.busapp.buss_api.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {
    private long totalUsers;
    private long totalOperators;
    private long totalBookings;
    private long totalTrips;
    private String totalRevenue;
    private long confirmedBookings;
    private long pendingBookings;
    private long cancelledBookings;
    private long completedBookings;
    private long activeOperators;
    private long inactiveOperators;
    private long activeUsers;
    private long inactiveUsers;
    private LocalDateTime lastUpdated;
}
