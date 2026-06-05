package com.busapp.buss_api.mapper;

import com.busapp.buss_api.dto.*;
import com.busapp.buss_api.entity.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ResponseMapper {

    public PickupDropoffPointResponse toPointResponse(PickupDropoffPoint p) {
        return PickupDropoffPointResponse.builder()
                .id(p.getId())
                .operatorId(p.getOperator() != null ? p.getOperator().getId() : null)
                .routeId(p.getRoute() != null ? p.getRoute().getId() : null)
                .pointType(p.getPointType() != null ? p.getPointType().name() : null)
                .name(p.getName())
                .address(p.getAddress())
                .latitude(p.getLatitude())
                .longitude(p.getLongitude())
                .pickupTimeNote(p.getPickupTimeNote())
                .isActive(p.getIsActive())
                .build();
    }

    public List<PickupDropoffPointResponse> toPointResponseList(List<PickupDropoffPoint> points) {
        return points.stream().map(this::toPointResponse).collect(Collectors.toList());
    }

    public TripResponse toTripResponse(Trip t) {
        BusOperator op = t.getOperator();
        Route r = t.getRoute();
        VehicleType vt = t.getVehicleType();

        return TripResponse.builder()
                .id(t.getId())
                .operatorId(op.getId())
                .operatorName(op.getName())
                .operatorLogo(op.getLogoUrl())
                // ÉP KIỂU TƯỜNG MINH SANG DOUBLE
                .operatorRating(op.getAvgRating() != null ? Double.valueOf(op.getAvgRating().toString()) : 0.0)
                .operatorTotalReviews(op.getTotalReviews() != null ? op.getTotalReviews() : 0)
                .routeId(r.getId())
                .departureProvince(r.getDepartureProvince() != null ? r.getDepartureProvince().getName() : "")
                .destinationProvince(r.getDestinationProvince() != null ? r.getDestinationProvince().getName() : "")
                .distanceKm(r.getDistanceKm() != null ? Double.valueOf(r.getDistanceKm().toString()) : 0.0)
                .durationHours(r.getDurationHours() != null ? Double.valueOf(r.getDurationHours().toString()) : 0.0)
                .vehicleTypeId(vt.getId())
                .vehicleTypeName(vt.getName())
                .seatCount(vt.getSeatCount())
                .seatLayout(vt.getSeatLayout())
                .departureDate(t.getDepartureDate())
                .departureTime(t.getDepartureTime())
                .arrivalTime(t.getArrivalTime())
                .price(t.getPrice())
                .availableSeats(t.getAvailableSeats())
                .totalSeats(t.getTotalSeats())
                .status(t.getStatus() != null ? t.getStatus().name() : "active")
                .build();
    }

    public List<TripResponse> toTripResponseList(List<Trip> trips) {
        return trips.stream().map(this::toTripResponse).collect(Collectors.toList());
    }

    public BookingResponse toBookingResponse(Booking b) {
        Trip trip = b.getTrip();
        Seat seat = b.getSeat();

        return BookingResponse.builder()
                .id(b.getId())
                .bookingCode(b.getBookingCode())
                .userId(b.getUser().getId())
                .userFullName(b.getUser().getFullName())
                .tripId(trip.getId())
                .departureProvince(trip.getRoute().getDepartureProvince().getName())
                .destinationProvince(trip.getRoute().getDestinationProvince().getName())
                .departureDate(trip.getDepartureDate().toString())
                .departureTime(trip.getDepartureTime().toString())
                .operatorName(trip.getOperator().getName())
                .seatId(seat.getId())
                .seatCode(seat.getSeatCode())
                .floor(seat.getFloor())
                .rowNum(seat.getRowNum())
                .colNum(seat.getColNum())
                .pickupPoint(b.getPickupPoint() != null ? b.getPickupPoint().getName() : null)
                .dropoffPoint(b.getDropoffPoint() != null ? b.getDropoffPoint().getName() : null)
                .passengerName(b.getPassengerName())
                .passengerPhone(b.getPassengerPhone())
                .passengerEmail(b.getPassengerEmail())
                .ticketPrice(b.getTicketPrice())
                .paymentMethod(b.getPaymentMethod().name())
                .paymentStatus(b.getPaymentStatus().name())
                .bookingStatus(b.getBookingStatus().name())
                .ticketType(b.getTicketType().name())
                .cancelDeadline(b.getCancelDeadline())
                .cancelledAt(b.getCancelledAt())
                .createdAt(b.getCreatedAt())
                .build();
    }

    public SeatResponse toSeatResponse(Seat seat) {
        return SeatResponse.builder()
                .id(seat.getId())
                .tripId(seat.getTrip().getId())
                .seatCode(seat.getSeatCode())
                .floor(seat.getFloor())
                .rowNum(seat.getRowNum())
                .colNum(seat.getColNum())
                .status(seat.getStatus().name())
                .lockedBy(seat.getLockedBy())
                .build();
    }

    public OperatorResponse toOperatorResponse(BusOperator o) {
        OperatorResponse.OperatorRouteResponse routeResponse = null;
        if (o.getTrips() != null && !o.getTrips().isEmpty()) {
            var trip = o.getTrips().get(0);
            if (trip.getRoute() != null) {
                routeResponse = OperatorResponse.OperatorRouteResponse.builder()
                        .routeId(trip.getRoute().getId())
                        .departure(trip.getRoute().getDepartureProvince() != null ? trip.getRoute().getDepartureProvince().getName() : null)
                        .destination(trip.getRoute().getDestinationProvince() != null ? trip.getRoute().getDestinationProvince().getName() : null)
                        .durationHours(trip.getRoute().getDurationHours() != null ? Double.valueOf(trip.getRoute().getDurationHours().toString()) : 0.0)
                        .build();
            }
        }

        return OperatorResponse.builder()
                .id(o.getId())
                .name(o.getName())
                .phone(o.getPhone())
                .email(o.getEmail())
                .description(o.getDescription())
                .logoUrl(o.getLogoUrl())
                .amenities(o.getAmenities())
                .cancellationPolicy(o.getCancellationPolicy())
                .avgRating(o.getAvgRating() != null ? Double.valueOf(o.getAvgRating().toString()) : 0.0)
                .totalReviews(o.getTotalReviews() != null ? o.getTotalReviews() : 0)
                .isActive(o.getIsActive())
                .routes(routeResponse != null ? List.of(routeResponse) : List.of())
                .build();
    }

    public ReviewResponse toReviewResponse(Review review) {
        User user = review.getUser();
        Trip trip = review.getTrip();

        return ReviewResponse.builder()
                .id(review.getId())
                .userId(user.getId())
                .userFullName(user.getFullName())
                .userAvatarUrl(user.getAvatarUrl())
                .tripId(trip.getId())
                .operatorId(review.getOperator().getId())
                .operatorName(review.getOperator().getName())
                .rating(review.getRating())
                .comment(review.getComment())
                .isVerified(review.getIsVerified())
                .createdAt(review.getCreatedAt())
                .build();
    }

    public UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole().getRoleName())
                .isActive(user.getIsActive())
                .build();
    }

    public UserAdminResponse toUserAdminResponse(User user) {
        return UserAdminResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole().getRoleName())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
