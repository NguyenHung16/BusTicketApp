package com.busapp.buss_api.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponse {
    private Integer id;
    private Integer userId;
    private String userFullName;
    private String userAvatarUrl;
    private Integer tripId;
    private Integer operatorId;
    private String operatorName;
    private Integer rating;
    private String comment;
    private Boolean isVerified;
    private LocalDateTime createdAt;
}
