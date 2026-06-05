package com.busapp.buss_api.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserManagementResponse {
    private Integer id;
    private String fullName;
    private String email;
    private String phone;
    private String avatarUrl;
    private String roleName;
    private Boolean isActive;
    private Integer totalBookings;
    private Integer totalReviews;
    private String createdAt;
}
