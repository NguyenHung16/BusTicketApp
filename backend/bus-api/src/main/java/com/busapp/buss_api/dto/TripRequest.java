package com.busapp.buss_api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripRequest {
    @NotNull(message = "Mã nhà xe không được trống")
    private Integer operatorId;

    @NotNull(message = "Mã tuyến không được trống")
    private Integer routeId;

    @NotNull(message = "Mã loại xe không được trống")
    private Integer vehicleTypeId;

    @NotNull(message = "Ngày khởi hành không được trống")
    private LocalDate departureDate;

    @NotNull(message = "Giờ khởi hành không được trống")
    private String departureTime;

    private String arrivalTime;
    private String price;
    private Integer availableSeats;
    private String status;
}
