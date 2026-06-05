package com.busapp.buss_api.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatMapResponse {
    private Integer tripId;
    private Integer vehicleTypeId;
    private String vehicleTypeName;
    private String seatLayout;
    private Integer floorCount;
    private Integer totalSeats;
    private Integer availableSeats;
    private List<SeatResponse> seats;
}
