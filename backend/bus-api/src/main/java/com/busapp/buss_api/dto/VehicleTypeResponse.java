package com.busapp.buss_api.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleTypeResponse {
    private Integer id;
    private String name;
    private Integer seatCount;
    private String seatLayout;
    private Integer floorCount;
    private String description;
}
