package com.busapp.buss_api.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PickupDropoffPointResponse {
    private Integer id;
    private Integer operatorId;
    private Integer routeId;
    private String pointType; // pickup, dropoff
    private String name;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String pickupTimeNote;
    private Boolean isActive;
}
