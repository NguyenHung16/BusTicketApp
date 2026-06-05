package com.busapp.buss_api.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {
    private Integer id;
    private String bookingCode;
    private Integer userId;
    private String userFullName;

    private Integer tripId;
    private String departureProvince;
    private String destinationProvince;
    private String departureDate;
    private String departureTime;
    private String operatorName;

    private Integer seatId;
    private String seatCode;
    private Integer floor;
    private Integer rowNum;
    private Integer colNum;

    private String pickupPoint;
    private String dropoffPoint;

    private String passengerName;
    private String passengerPhone;
    private String passengerEmail;

    private BigDecimal ticketPrice;
    private String paymentMethod;
    private String paymentStatus;
    private String bookingStatus;
    private String ticketType;
    private LocalDateTime cancelDeadline;
    private LocalDateTime cancelledAt;
    private LocalDateTime createdAt;
}
