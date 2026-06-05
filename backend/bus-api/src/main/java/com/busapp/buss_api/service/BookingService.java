package com.busapp.buss_api.service;

import com.busapp.buss_api.dto.*;
import com.busapp.buss_api.entity.*;
import com.busapp.buss_api.exception.BadRequestException;
import com.busapp.buss_api.exception.ResourceNotFoundException;
import com.busapp.buss_api.mapper.ResponseMapper;
import com.busapp.buss_api.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TripRepository tripRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final PickupDropoffPointRepository pointRepository;
    private final SeatService seatService;
    private final ResponseMapper mapper;

    @Transactional
    public BookingResponse createBooking(Integer userId, BookingRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Trip trip = tripRepository.findById(request.getTripId())
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", request.getTripId()));

        Seat seat = seatRepository.findById(request.getSeatId())
                .orElseThrow(() -> new ResourceNotFoundException("Seat", "id", request.getSeatId()));

        if (!seat.getTrip().getId().equals(trip.getId())) {
            throw new BadRequestException("Ghế không thuộc chuyến đi này");
        }

        if (seat.getStatus() != Seat.SeatStatus.available && seat.getStatus() != Seat.SeatStatus.locked) {
            throw new BadRequestException("Ghế " + seat.getSeatCode() + " không còn trống");
        }

        if (bookingRepository.findActiveBooking(trip.getId(), seat.getId()).isPresent()) {
            throw new BadRequestException("Ghế " + seat.getSeatCode() + " đã có người đặt");
        }

        PickupDropoffPoint pickupPoint = null;
        if (request.getPickupPointId() != null) {
            pickupPoint = pointRepository.findById(request.getPickupPointId()).orElse(null);
        }

        PickupDropoffPoint dropoffPoint = null;
        if (request.getDropoffPointId() != null) {
            dropoffPoint = pointRepository.findById(request.getDropoffPointId()).orElse(null);
        }

        String bookingCode = generateBookingCode();
        LocalDateTime cancelDeadline = trip.getDepartureDate()
                .atTime(trip.getDepartureTime())
                .minusHours(2);

        Booking booking = Booking.builder()
                .bookingCode(bookingCode)
                .user(user)
                .trip(trip)
                .seat(seat)
                .pickupPoint(pickupPoint)
                .dropoffPoint(dropoffPoint)
                .passengerName(request.getPassengerName())
                .passengerPhone(request.getPassengerPhone())
                .passengerEmail(request.getPassengerEmail())
                .ticketPrice(request.getTicketPrice() != null ? request.getTicketPrice() : trip.getPrice())
                .paymentMethod(Booking.PaymentMethod.valueOf(request.getPaymentMethod()))
                .paymentStatus(Booking.PaymentStatus.pending)
                .bookingStatus(Booking.BookingStatus.pending)
                .ticketType(Booking.TicketType.valueOf(request.getTicketType()))
                .cancelDeadline(cancelDeadline)
                .build();

        Booking saved = bookingRepository.save(booking);
        seatService.bookSeat(trip.getId(), seat.getSeatCode());

        log.info("Created booking {} for user {} on trip {} seat {}",
                bookingCode, userId, trip.getId(), seat.getSeatCode());

        return mapToResponse(saved);
    }

    @Transactional
    public BookingResponse confirmBooking(String bookingCode, Integer userId, String role) {
        Booking booking = bookingRepository.findByBookingCode(bookingCode)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "code", bookingCode));

        // Kiểm tra quyền: ADMIN hoặc OPERATOR sở hữu trip
        if ("OPERATOR".equals(role)) {
            Integer operatorUserId = booking.getTrip().getOperator().getUserId();
            if (operatorUserId == null || !operatorUserId.equals(userId)) {
                throw new BadRequestException("Bạn không có quyền xác nhận đặt vé này");
            }
        }
        // ADMIN có quyền confirm tất cả (không cần check)

        if (booking.getBookingStatus() == Booking.BookingStatus.cancelled) {
            throw new BadRequestException("Không thể xác nhận đặt vé đã bị hủy");
        }
        if (booking.getBookingStatus() == Booking.BookingStatus.confirmed) {
            throw new BadRequestException("Đặt vé đã được xác nhận trước đó");
        }

        booking.setBookingStatus(Booking.BookingStatus.confirmed);
        Booking saved = bookingRepository.save(booking);
        log.info("Confirmed booking: {} by user {}", bookingCode, userId);
        return mapToResponse(saved);
    }

    @Transactional
    public BookingResponse cancelBooking(String bookingCode, Integer userId) {
        Booking booking = bookingRepository.findByBookingCode(bookingCode)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "code", bookingCode));

        if (!booking.getUser().getId().equals(userId)) {
            throw new BadRequestException("Bạn không có quyền hủy đặt vé này");
        }

        if (booking.getBookingStatus() == Booking.BookingStatus.cancelled) {
            throw new BadRequestException("Đặt vé đã được hủy trước đó");
        }

        if (booking.getCancelDeadline() != null && LocalDateTime.now().isAfter(booking.getCancelDeadline())) {
            throw new BadRequestException("Đã quá thời hạn hủy đặt vé");
        }

        booking.setBookingStatus(Booking.BookingStatus.cancelled);
        booking.setCancelledAt(LocalDateTime.now());

        Seat seat = booking.getSeat();
        seat.setStatus(Seat.SeatStatus.available);
        seatRepository.save(seat);

        if (booking.getPaymentStatus() == Booking.PaymentStatus.paid) {
            booking.setPaymentStatus(Booking.PaymentStatus.refunded);
        }

        Trip trip = booking.getTrip();
        trip.setAvailableSeats(seatRepository.countAvailableSeats(trip.getId()));
        if (trip.getStatus() == Trip.TripStatus.full) {
            trip.setStatus(Trip.TripStatus.active);
        }
        tripRepository.save(trip);

        Booking saved = bookingRepository.save(booking);
        log.info("Cancelled booking: {}", bookingCode);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public BookingResponse getBookingByCode(String bookingCode) {
        Booking booking = bookingRepository.findByBookingCode(bookingCode)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "code", bookingCode));
        return mapToResponse(booking);
    }

    @Transactional(readOnly = true)
    public PageResponse<BookingResponse> getMyBookings(Integer userId, Pageable pageable) {
        Page<Booking> page = bookingRepository.findByUserId(userId, pageable);
        return PageResponse.from(page, mapper::toBookingResponse);
    }

    @Transactional
    public BookingResponse updatePaymentStatus(String bookingCode, String paymentStatus) {
        Booking booking = bookingRepository.findByBookingCode(bookingCode)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "code", bookingCode));

        booking.setPaymentStatus(Booking.PaymentStatus.valueOf(paymentStatus));
        if (Booking.PaymentStatus.paid.equals(booking.getPaymentStatus())) {
            booking.setBookingStatus(Booking.BookingStatus.confirmed);
        }

        Booking saved = bookingRepository.save(booking);
        log.info("Updated payment status for booking {}: {}", bookingCode, paymentStatus);
        return mapToResponse(saved);
    }

    private String generateBookingCode() {
        String code;
        do {
            code = "BUS" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (bookingRepository.existsByBookingCode(code));
        return code;
    }

    private BookingResponse mapToResponse(Booking b) {
        return mapper.toBookingResponse(b);
    }
}
