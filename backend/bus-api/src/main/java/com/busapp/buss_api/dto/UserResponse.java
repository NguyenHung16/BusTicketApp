package com.busapp.buss_api.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Integer id;
    private String fullName;
    private String email;
    private String phone;
    private String avatarUrl;
    private String role;
    private Boolean isActive;
}
