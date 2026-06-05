package com.busapp.buss_api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bus_operators")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusOperator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 15)
    private String phone;

    @Column(length = 100)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(length = 500)
    private String amenities;

    @Column(name = "cancellation_policy", columnDefinition = "TEXT")
    private String cancellationPolicy;

    @Column(name = "avg_rating")
    private Float avgRating = 0.0f;

    @Column(name = "total_reviews")
    private Integer totalReviews = 0;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "user_id")
    private Integer userId;

    @OneToMany(mappedBy = "operator")
    private List<Trip> trips;

    @OneToMany(mappedBy = "operator")
    private List<PickupDropoffPoint> points;

    @OneToMany(mappedBy = "operator")
    private List<Review> reviews;
}
