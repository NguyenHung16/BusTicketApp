package com.busapp.buss_api.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperatorApprovalResponse {
    private Integer id;
    private String name;
    private String phone;
    private String email;
    private String description;
    private String logoUrl;
    private Boolean isActive;
    private Integer userId;
    private Integer totalTrips;
    private Integer totalReviews;
    private Float avgRating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
