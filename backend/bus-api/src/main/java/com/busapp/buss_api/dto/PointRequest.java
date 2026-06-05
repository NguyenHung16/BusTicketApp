package com.busapp.buss_api.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointRequest {
    @NotNull(message = "ID nhà xe không được trống")
    private Integer operatorId;

    @NotNull(message = "ID tuyến đường không được trống")
    private Integer routeId;

    @NotBlank(message = "Loại điểm không được trống")
    @Pattern(regexp = "pickup|dropoff", message = "Loại điểm phải là 'pickup' hoặc 'dropoff'")
    private String pointType;

    @NotBlank(message = "Tên điểm không được trống")
    @Size(max = 200, message = "Tên điểm không quá 200 ký tự")
    private String name;

    @Size(max = 300, message = "Địa chỉ không quá 300 ký tự")
    private String address;

    private Double latitude;
    private Double longitude;

    @Size(max = 100, message = "Ghi chú không quá 100 ký tự")
    private String pickupTimeNote;
}
