package com.busapp.buss_api.repository;

import com.busapp.buss_api.entity.BusOperator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusOperatorRepository extends JpaRepository<BusOperator, Integer> {

    Page<BusOperator> findByIsActiveTrue(Pageable pageable);

    Page<BusOperator> findByIsActiveFalse(Pageable pageable);

    Optional<BusOperator> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("""
        SELECT DISTINCT o FROM BusOperator o
        LEFT JOIN FETCH o.trips t
        LEFT JOIN FETCH t.route r
        LEFT JOIN FETCH r.departureProvince
        LEFT JOIN FETCH r.destinationProvince
        WHERE o.id = :id
        """)
    Optional<BusOperator> findByIdWithTrips(@Param("id") Integer id);

    @Query("""
        SELECT o FROM BusOperator o
        LEFT JOIN FETCH o.reviews r
        WHERE o.id = :id
        """)
    Optional<BusOperator> findByIdWithReviews(@Param("id") Integer id);

    @Query("SELECT o FROM BusOperator o WHERE o.isActive = true ORDER BY o.avgRating DESC")
    List<BusOperator> findTopRated();

    @Query("""
        SELECT DISTINCT o FROM BusOperator o
        JOIN o.trips t
        JOIN t.route r
        JOIN r.departureProvince dp
        WHERE dp.slug = :departure
        AND o.isActive = true
        """)
    List<BusOperator> findOperatorsByDeparture(@Param("departure") String departure);

    long countByIsActiveTrue();

    long countByIsActiveFalse();
}
