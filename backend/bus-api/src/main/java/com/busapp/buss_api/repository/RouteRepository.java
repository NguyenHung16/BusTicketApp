package com.busapp.buss_api.repository;

import com.busapp.buss_api.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RouteRepository extends JpaRepository<Route, Integer> {

    Optional<Route> findByDepartureProvinceIdAndDestinationProvinceId(
            Integer departureId, Integer destinationId);

    List<Route> findByIsPopularTrue();

    @Query("""
        SELECT r FROM Route r
        JOIN FETCH r.departureProvince
        JOIN FETCH r.destinationProvince
        WHERE r.id = :id
        """)
    Optional<Route> findByIdWithProvinces(@Param("id") Integer id);

    @Query("""
        SELECT r FROM Route r
        JOIN FETCH r.departureProvince dp
        JOIN FETCH r.destinationProvince dp2
        WHERE dp.slug = :departure
        AND dp2.slug = :destination
        """)
    List<Route> findByProvinceSlugs(
            @Param("departure") String departure,
            @Param("destination") String destination);
}
