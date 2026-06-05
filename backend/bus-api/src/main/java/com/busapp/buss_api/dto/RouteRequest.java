package com.busapp.buss_api.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteRequest {
    private Integer departureProvinceId;
    private Integer destinationProvinceId;
    private Integer distanceKm;
    private Float durationHours;
    private Boolean isPopular;
}
