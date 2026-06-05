package com.busapp.buss_api.repository;

import com.busapp.buss_api.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Integer> {

    List<Seat> findByTripIdOrderByFloorAscRowNumAscColNumAsc(Integer tripId);

    Optional<Seat> findByTripIdAndSeatCode(Integer tripId, String seatCode);

    @Query("SELECT COUNT(s) FROM Seat s WHERE s.trip.id = :tripId AND s.status = 'available'")
    Integer countAvailableSeats(@Param("tripId") Integer tripId);

    @Query("SELECT COUNT(s) FROM Seat s WHERE s.trip.id = :tripId")
    Integer countTotalSeats(@Param("tripId") Integer tripId);

    @Modifying // BẮT BUỘC cho các thao tác xóa/sửa
    void deleteByTripId(Integer tripId);
}
