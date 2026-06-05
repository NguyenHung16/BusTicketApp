package com.busapp.buss_api.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperatorRequest {
    private String name;
    private String phone;
    private String email;
    private String description;
    private String amenities;
    private String cancellationPolicy;
    private Boolean isActive;
}
