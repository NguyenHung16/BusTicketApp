package com.busapp.buss_api.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleTypeRequest {
    @NotBlank(message = "Tên loại xe không được trống")
    @Size(max = 100, message = "Tên loại xe không quá 100 ký tự")
    private String name;

    @NotNull(message = "Số ghế không được trống")
    @Min(value = 1, message = "Số ghế phải lớn hơn 0")
    private Integer seatCount;

    @Pattern(regexp = "^\\d+-\\d+$|^\\d+-\\d+-\\d+$", message = "Seat layout phải theo format: 2-2, 2-1, 1-1, 2-0-2, v.v.")
    private String seatLayout;

    @Min(value = 1, message = "Số tầng phải lớn hơn 0")
    private Integer floorCount = 1;

    @Size(max = 255, message = "Mô tả không quá 255 ký tự")
    private String description;
}
