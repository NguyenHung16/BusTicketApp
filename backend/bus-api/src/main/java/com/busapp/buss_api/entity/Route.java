package com.busapp.buss_api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "routes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"departure_province_id", "destination_province_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departure_province_id", nullable = false)
    private Province departureProvince;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_province_id", nullable = false)
    private Province destinationProvince;

    @Column(name = "distance_km")
    private Integer distanceKm;

    @Column(name = "duration_hours")
    private Float durationHours;

    @Column(name = "is_popular")
    private Boolean isPopular = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "route")
    private List<Trip> trips;

    @OneToMany(mappedBy = "route")
    private List<PickupDropoffPoint> points;
}
