package com.busapp.buss_api.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {
    @Size(max = 100, message = "Họ tên không quá 100 ký tự")
    private String fullName;

    @Email(message = "Email không hợp lệ")
    private String email;

    @Size(min = 6, max = 20, message = "Số điện thoại phải từ 6-20 ký tự")
    private String phone;

    private String avatarUrl;

    private Boolean isActive;
}
