package com.busapp.buss_api.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProfileRequest {
    private String fullName;
    private String phone;
    private String email;
    private String avatarUrl;
}
