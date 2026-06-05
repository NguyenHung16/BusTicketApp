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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TripService {

    private final TripRepository tripRepository;
    private final BusOperatorRepository operatorRepository;
    private final RouteRepository routeRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final PickupDropoffPointRepository pointRepository;
    private final SeatService seatService;
    private final ResponseMapper mapper;

    @Transactional(readOnly = true)
    public PageResponse<TripResponse> searchTrips(TripSearchRequest request, Pageable pageable) {
        Route route = routeRepository.findByProvinceSlugs(request.getDeparture(), request.getDestination())
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy tuyến đường từ " + request.getDeparture() + " đến " + request.getDestination()));

        Sort sort = buildSort(request);
        Page<Trip> trips = tripRepository.findAvailableTrips(
                route.getId(),
                request.getDepartureDate(),
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort));

        return PageResponse.from(trips, mapper::toTripResponse);
    }

    @Transactional(readOnly = true)
    public List<TripResponse> getAll() {
        return tripRepository.findAllFutureTrips().stream()
                .map(mapper::toTripResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TripResponse getById(Integer id) {
        Trip trip = tripRepository.findByIdWithDetails(id);
        if (trip == null) {
            throw new ResourceNotFoundException("Trip", "id", id);
        }
        return mapper.toTripResponse(trip);
    }

    @Transactional
    public TripResponse create(TripRequest request) {
        log.info("Bắt đầu tạo chuyến xe: {}", request);
        
        BusOperator operator = operatorRepository.findById(request.getOperatorId())
                .orElseThrow(() -> new ResourceNotFoundException("Operator", "id", request.getOperatorId()));
        Route route = routeRepository.findById(request.getRouteId())
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", request.getRouteId()));
        VehicleType vehicleType = vehicleTypeRepository.findById(request.getVehicleTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("VehicleType", "id", request.getVehicleTypeId()));

        try {
            Trip trip = Trip.builder()
                    .operator(operator)
                    .route(route)
                    .vehicleType(vehicleType)
                    .departureDate(request.getDepartureDate()) // Sử dụng trực tiếp vì đã là LocalDate
                    .departureTime(parseTime(request.getDepartureTime()))
                    .arrivalTime(request.getArrivalTime() != null ? parseTime(request.getArrivalTime()) : null)
                    .price(new BigDecimal(request.getPrice().replaceAll("[^0-9]", "")))
                    .availableSeats(vehicleType.getSeatCount())
                    .totalSeats(vehicleType.getSeatCount())
                    .status(Trip.TripStatus.active)
                    .build();

            Trip saved = tripRepository.save(trip);
            seatService.generateSeatsForTrip(saved, vehicleType);
            return mapper.toTripResponse(saved);
        } catch (Exception e) {
            log.error("Lỗi khi tạo chuyến xe: ", e);
            throw new RuntimeException("Lỗi lưu chuyến xe: " + e.getMessage());
        }
    }

    private LocalTime parseTime(String timeStr) {
        if (timeStr == null || timeStr.isBlank()) return null;
        try {
            if (timeStr.length() == 5) return LocalTime.parse(timeStr + ":00");
            return LocalTime.parse(timeStr);
        } catch (DateTimeParseException e) {
            log.error("Lỗi parse thời gian: {}", timeStr);
            return null;
        }
    }

    @Transactional
    public TripResponse update(Integer id, TripRequest request) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", id));

        if (request.getDepartureDate() != null) trip.setDepartureDate(request.getDepartureDate());
        if (request.getDepartureTime() != null) trip.setDepartureTime(parseTime(request.getDepartureTime()));
        if (request.getArrivalTime() != null) trip.setArrivalTime(parseTime(request.getArrivalTime()));
        if (request.getPrice() != null) trip.setPrice(new BigDecimal(request.getPrice().replaceAll("[^0-9]", "")));
        
        Trip saved = tripRepository.save(trip);
        return mapper.toTripResponse(saved);
    }

    @Transactional
    public void delete(Integer id) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", id));
        trip.setStatus(Trip.TripStatus.cancelled);
        tripRepository.save(trip);
    }

    @Transactional(readOnly = true)
    public List<TripResponse> getByOperator(Integer operatorId, LocalDate date) {
        return tripRepository.findByOperatorIdAndDepartureDateWithDetails(operatorId, date).stream()
                .map(mapper::toTripResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PickupDropoffPointResponse> getPickupPoints(Integer tripId) {
        return getPoints(tripId, PickupDropoffPoint.PointType.pickup);
    }

    @Transactional(readOnly = true)
    public List<PickupDropoffPointResponse> getDropoffPoints(Integer tripId) {
        return getPoints(tripId, PickupDropoffPoint.PointType.dropoff);
    }

    private List<PickupDropoffPointResponse> getPoints(Integer tripId, PickupDropoffPoint.PointType type) {
        Trip trip = tripRepository.findByIdWithDetails(tripId);
        if (trip == null) throw new ResourceNotFoundException("Trip", "id", tripId);
        return mapper.toPointResponseList(pointRepository.findByRouteIdAndPointType(trip.getRoute().getId(), type));
    }

    private Sort buildSort(TripSearchRequest request) {
        if (request.getSortBy() == null) return Sort.unsorted();
        Sort.Direction dir = "asc".equalsIgnoreCase(request.getSortDir())
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        return switch (request.getSortBy().toLowerCase()) {
            case "price" -> Sort.by(dir, "price");
            case "departuretime" -> Sort.by(dir, "departureTime");
            case "rating" -> Sort.by(dir, "operator.avgRating");
            default -> Sort.unsorted();
        };
    }
}
