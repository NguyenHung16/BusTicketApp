package com.busapp.buss_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequest {

    @NotNull(message = "Mã chuyến không được trống")
    private Integer tripId;

    @NotNull(message = "Mã ghế không được trống")
    private Integer seatId;

    private Integer pickupPointId;
    private Integer dropoffPointId;

    @NotBlank(message = "Tên hành khách không được trống")
    private String passengerName;

    @NotBlank(message = "Số điện thoại hành khách không được trống")
    private String passengerPhone;

    private String passengerEmail;

    @NotBlank(message = "Loại vé không được trống")
    private String ticketType; // one_way, round_trip

    @NotBlank(message = "Phương thức thanh toán không được trống")
    private String paymentMethod; // cod, momo, zalopay, bank_transfer

    private BigDecimal ticketPrice;
}
