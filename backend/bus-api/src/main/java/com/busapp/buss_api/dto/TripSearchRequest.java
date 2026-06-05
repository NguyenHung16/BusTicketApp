package com.busapp.buss_api.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripSearchRequest {
    private String departure;
    private String destination;
    private LocalDate departureDate;
    private String sortBy;   // price, departureTime, rating
    private String sortDir;  // asc, desc
    private Integer page = 0;
    private Integer size = 10;
}
