package com.busapp.buss_api.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopOperatorResponse {
    private Integer id;
    private String name;
    private String logoUrl;
    private Float avgRating;
    private Integer totalReviews;
    private Long totalTrips;
    private Long totalBookings;
    private BigDecimal totalRevenue;
}
