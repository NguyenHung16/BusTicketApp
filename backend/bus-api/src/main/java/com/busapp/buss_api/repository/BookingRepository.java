package com.busapp.buss_api.repository;

import com.busapp.buss_api.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    Optional<Booking> findByBookingCode(String bookingCode);

    Page<Booking> findByUserId(Integer userId, Pageable pageable);

    List<Booking> findByTripId(Integer tripId);

    @Query("SELECT b FROM Booking b WHERE b.trip.id = :tripId AND b.seat.id = :seatId AND b.bookingStatus NOT IN ('cancelled')")
    Optional<Booking> findActiveBooking(
            @Param("tripId") Integer tripId,
            @Param("seatId") Integer seatId);

    @Query("""
        SELECT b FROM Booking b
        JOIN FETCH b.trip t
        JOIN FETCH t.operator
        JOIN FETCH t.route
        JOIN FETCH b.seat
        WHERE b.id = :id
        """)
    Optional<Booking> findByIdWithDetails(@Param("id") Integer id);

    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.bookingStatus = 'completed'")
    Page<Booking> findCompletedBookingsByUser(@Param("userId") Integer userId, Pageable pageable);

    boolean existsByBookingCode(String bookingCode);

    // Methods for AdminService
    @Query("SELECT SUM(b.ticketPrice) FROM Booking b WHERE b.paymentStatus = 'paid' OR b.paymentStatus = 'completed'")
    BigDecimal sumTotalRevenue();

    long countByBookingStatus(Booking.BookingStatus status);

    @Query("SELECT SUM(b.ticketPrice) FROM Booking b WHERE b.trip.operator.id = :operatorId AND (b.paymentStatus = 'paid' OR b.paymentStatus = 'completed')")
    BigDecimal sumRevenueByOperator(@Param("operatorId") Integer operatorId);

    long countByTripOperatorId(Integer operatorId);

    Page<Booking> findAllByBookingStatus(Booking.BookingStatus status, Pageable pageable);

    Page<Booking> findAllByPaymentStatus(Booking.PaymentStatus status, Pageable pageable);
}
