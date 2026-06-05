package com.busapp.buss_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "vehicle_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "seat_count", nullable = false)
    private Integer seatCount;

    @Column(name = "seat_layout", length = 20)
    private String seatLayout;

    @Column(name = "floor_count", nullable = false)
    private Integer floorCount = 1;

    @Column(length = 255)
    private String description;

    @OneToMany(mappedBy = "vehicleType")
    private List<Trip> trips;
}
