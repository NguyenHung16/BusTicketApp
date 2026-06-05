package com.busapp.buss_api.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatResponse {
    private Integer id;
    private Integer tripId;
    private String seatCode;
    private Integer floor;
    private Integer rowNum;
    private Integer colNum;
    private String status;
    private Integer lockedBy;
}
