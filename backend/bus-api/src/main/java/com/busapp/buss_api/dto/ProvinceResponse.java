package com.busapp.buss_api.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProvinceResponse {
    private Integer id;
    private String name;
    private String slug;
}
