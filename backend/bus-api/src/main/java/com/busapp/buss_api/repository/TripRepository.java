package com.busapp.buss_api.repository;

import com.busapp.buss_api.entity.BusOperator;
import com.busapp.buss_api.entity.Trip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<Trip, Integer> {

    @Query("SELECT t FROM Trip t WHERE t.departureDate >= CURRENT_DATE ORDER BY t.departureDate ASC, t.departureTime ASC")
    List<Trip> findAllFutureTrips();

    Page<Trip> findByRouteIdAndDepartureDateAndStatus(
            Integer routeId, LocalDate departureDate, Trip.TripStatus status, Pageable pageable);

    @Query("""
        SELECT t FROM Trip t
        JOIN FETCH t.operator o
        JOIN FETCH t.route r
        JOIN FETCH r.departureProvince dp
        JOIN FETCH r.destinationProvince dp2
        JOIN FETCH t.vehicleType vt
        WHERE t.route.id = :routeId
        AND t.departureDate = :date
        AND t.status = 'active'
        AND t.availableSeats > 0
        """)
    Page<Trip> findAvailableTrips(
            @Param("routeId") Integer routeId,
            @Param("date") LocalDate date,
            Pageable pageable);

    @Query("""
        SELECT t FROM Trip t
        JOIN FETCH t.operator o
        JOIN FETCH t.route r
        JOIN FETCH r.departureProvince
        JOIN FETCH r.destinationProvince
        JOIN FETCH t.vehicleType
        WHERE t.id = :id
        """)
    Trip findByIdWithDetails(@Param("id") Integer id);

    List<Trip> findByOperatorIdAndDepartureDate(Integer operatorId, LocalDate date);

    @Query("""
        SELECT t FROM Trip t
        JOIN FETCH t.operator
        JOIN FETCH t.route
        JOIN FETCH t.vehicleType
        WHERE t.operator.id = :operatorId
        AND t.departureDate = :date
        """)
    List<Trip> findByOperatorIdAndDepartureDateWithDetails(
            @Param("operatorId") Integer operatorId,
            @Param("date") LocalDate date);

    @Query("""
        SELECT t FROM Trip t
        JOIN FETCH t.route r
        WHERE r.departureProvince.slug = :departure
        AND r.destinationProvince.slug = :destination
        AND t.departureDate = :date
        AND t.status = 'active'
        AND t.availableSeats > 0
        """)
    List<Trip> searchTrips(
            @Param("departure") String departure,
            @Param("destination") String destination,
            @Param("date") LocalDate date);

    @Query("SELECT t FROM Trip t WHERE t.departureDate < CURRENT_DATE AND t.status = 'active'")
    List<Trip> findExpiredActiveTrips();

    @Query("""
        SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END
        FROM Trip t
        WHERE t.id = :tripId AND t.operator.id = :operatorId
        """)
    boolean isOperatorOfTrip(@Param("tripId") Integer tripId, @Param("operatorId") Integer operatorId);

    @Query("""
        SELECT o FROM BusOperator o
        JOIN o.trips t
        WHERE t.id = :tripId
        """)
    Optional<BusOperator> findOperatorByTripId(@Param("tripId") Integer tripId);
}
