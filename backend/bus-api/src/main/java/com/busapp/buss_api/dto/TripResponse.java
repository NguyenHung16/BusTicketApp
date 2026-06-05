package com.busapp.buss_api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripResponse {
    private Integer id;
    private Integer operatorId;
    private String operatorName;
    private String operatorLogo;
    private Double operatorRating; // Đảm bảo là Double
    private Integer operatorTotalReviews;

    private Integer routeId;
    private String departureProvince;
    private String destinationProvince;
    private Double distanceKm; // Đảm bảo là Double
    private Double durationHours; // Đảm bảo là Double

    private Integer vehicleTypeId;
    private String vehicleTypeName;
    private Integer seatCount;
    private String seatLayout;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate departureDate;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime departureTime;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime arrivalTime;

    private BigDecimal price;
    private Integer availableSeats;
    private Integer totalSeats;
    private String status;
    private List<PickupDropoffPointResponse> pickupPoints;
    private List<PickupDropoffPointResponse> dropoffPoints;
}
