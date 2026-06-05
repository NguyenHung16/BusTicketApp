package com.busapp.buss_api.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteResponse {
    private Integer id;
    private Integer departureProvinceId;
    private String departureProvinceName;
    private String departureProvinceSlug;
    private Integer destinationProvinceId;
    private String destinationProvinceName;
    private String destinationProvinceSlug;
    private Integer distanceKm;
    private Float durationHours;
    private Boolean isPopular;
}
