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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final UserRepository userRepository;
    private final BusOperatorRepository operatorRepository;
    private final BookingRepository bookingRepository;
    private final ProvinceRepository provinceRepository;
    private final RoleRepository roleRepository;
    private final TripRepository tripRepository;
    private final ResponseMapper mapper;

    // ==================== DASHBOARD ====================

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {
        long totalUsers = userRepository.count();
        long totalOperators = operatorRepository.count();
        long totalBookings = bookingRepository.count();
        long totalTrips = tripRepository.count();
        BigDecimal totalRevenue = bookingRepository.sumTotalRevenue();
        long confirmedBookings = bookingRepository.countByBookingStatus(Booking.BookingStatus.confirmed);
        long pendingBookings = bookingRepository.countByBookingStatus(Booking.BookingStatus.pending);
        long cancelledBookings = bookingRepository.countByBookingStatus(Booking.BookingStatus.cancelled);
        long completedBookings = bookingRepository.countByBookingStatus(Booking.BookingStatus.completed);
        long activeOperators = operatorRepository.countByIsActiveTrue();
        long inactiveOperators = operatorRepository.countByIsActiveFalse();
        long activeUsers = userRepository.countByIsActiveTrue();
        long inactiveUsers = userRepository.countByIsActiveFalse();

        return DashboardResponse.builder()
                .totalUsers(totalUsers)
                .totalOperators(totalOperators)
                .totalBookings(totalBookings)
                .totalTrips(totalTrips)
                .totalRevenue(totalRevenue != null ? totalRevenue.toString() : "0")
                .confirmedBookings(confirmedBookings)
                .pendingBookings(pendingBookings)
                .cancelledBookings(cancelledBookings)
                .completedBookings(completedBookings)
                .activeOperators(activeOperators)
                .inactiveOperators(inactiveOperators)
                .activeUsers(activeUsers)
                .inactiveUsers(inactiveUsers)
                .lastUpdated(LocalDateTime.now())
                .build();
    }

    @Transactional(readOnly = true)
    public PageResponse<TripResponse> getAllTrips(Pageable pageable) {
        Page<Trip> page = tripRepository.findAll(pageable);
        return PageResponse.<TripResponse>builder()
                .content(page.getContent().stream().map(mapper::toTripResponse).collect(Collectors.toList()))
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    // ==================== USER MANAGEMENT ====================

    @Transactional(readOnly = true)
    public PageResponse<UserManagementResponse> getAllUsers(Pageable pageable) {
        Page<User> page = userRepository.findAll(pageable);
        return PageResponse.<UserManagementResponse>builder()
                .content(page.getContent().stream().map(this::mapToUserManagement).collect(Collectors.toList()))
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public PageResponse<UserManagementResponse> getUsersByRole(String role, Pageable pageable) {
        Page<User> page = userRepository.findByRole_RoleName(role, pageable);
        return PageResponse.<UserManagementResponse>builder()
                .content(page.getContent().stream().map(this::mapToUserManagement).collect(Collectors.toList()))
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public UserManagementResponse getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return mapToUserManagement(user);
    }

    @Transactional
    public UserManagementResponse lockUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        if ("ADMIN".equals(user.getRole().getRoleName())) {
            throw new BadRequestException("Không thể khóa tài khoản Admin");
        }

        user.setIsActive(false);
        User saved = userRepository.save(user);
        log.info("Locked user: {}", id);
        return mapToUserManagement(saved);
    }

    @Transactional
    public UserManagementResponse unlockUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        user.setIsActive(true);
        User saved = userRepository.save(user);
        log.info("Unlocked user: {}", id);
        return mapToUserManagement(saved);
    }

    @Transactional
    public UserManagementResponse changeUserRole(Integer id, String newRole) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        Role role = roleRepository.findByRoleName(newRole)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", newRole));

        user.setRole(role);
        User saved = userRepository.save(user);
        log.info("Changed role of user {} to {}", id, newRole);
        return mapToUserManagement(saved);
    }

    // ==================== OPERATOR MANAGEMENT ====================

    @Transactional(readOnly = true)
    public PageResponse<OperatorApprovalResponse> getAllOperators(Pageable pageable) {
        Page<BusOperator> page = operatorRepository.findAll(pageable);
        return PageResponse.<OperatorApprovalResponse>builder()
                .content(page.getContent().stream().map(this::mapToOperatorApproval).collect(Collectors.toList()))
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public PageResponse<OperatorApprovalResponse> getPendingOperators(Pageable pageable) {
        Page<BusOperator> page = operatorRepository.findByIsActiveFalse(pageable);
        return PageResponse.<OperatorApprovalResponse>builder()
                .content(page.getContent().stream().map(this::mapToOperatorApproval).collect(Collectors.toList()))
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public PageResponse<OperatorApprovalResponse> getActiveOperators(Pageable pageable) {
        Page<BusOperator> page = operatorRepository.findByIsActiveTrue(pageable);
        return PageResponse.<OperatorApprovalResponse>builder()
                .content(page.getContent().stream().map(this::mapToOperatorApproval).collect(Collectors.toList()))
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public OperatorApprovalResponse getOperatorById(Integer id) {
        BusOperator operator = operatorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Operator", "id", id));
        return mapToOperatorApproval(operator);
    }

    @Transactional
    public OperatorApprovalResponse approveOperator(Integer id) {
        BusOperator operator = operatorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Operator", "id", id));
        operator.setIsActive(true);
        BusOperator saved = operatorRepository.save(operator);
        log.info("Approved operator: {}", id);
        return mapToOperatorApproval(saved);
    }

    @Transactional
    public OperatorApprovalResponse rejectOperator(Integer id) {
        BusOperator operator = operatorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Operator", "id", id));
        operator.setIsActive(false);
        BusOperator saved = operatorRepository.save(operator);
        log.info("Rejected operator: {}", id);
        return mapToOperatorApproval(saved);
    }

    @Transactional
    public OperatorApprovalResponse deactivateOperator(Integer id) {
        BusOperator operator = operatorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Operator", "id", id));
        operator.setIsActive(false);
        BusOperator saved = operatorRepository.save(operator);
        log.info("Deactivated operator: {}", id);
        return mapToOperatorApproval(saved);
    }

    @Transactional
    public OperatorApprovalResponse activateOperator(Integer id) {
        BusOperator operator = operatorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Operator", "id", id));
        operator.setIsActive(true);
        BusOperator saved = operatorRepository.save(operator);
        log.info("Activated operator: {}", id);
        return mapToOperatorApproval(saved);
    }

    @Transactional(readOnly = true)
    public List<TopOperatorResponse> getTopOperators() {
        List<BusOperator> operators = operatorRepository.findTopRated();
        return operators.stream().map(o -> {
            BigDecimal revenue = bookingRepository.sumRevenueByOperator(o.getId());
            long bookings = bookingRepository.countByTripOperatorId(o.getId());
            long trips = o.getTrips() != null ? o.getTrips().size() : 0L;
            return TopOperatorResponse.builder()
                    .id(o.getId())
                    .name(o.getName())
                    .logoUrl(o.getLogoUrl())
                    .avgRating(o.getAvgRating())
                    .totalReviews(o.getTotalReviews())
                    .totalTrips(trips)
                    .totalBookings(bookings)
                    .totalRevenue(revenue != null ? revenue : BigDecimal.ZERO)
                    .build();
        }).collect(Collectors.toList());
    }

    // ==================== BOOKING MANAGEMENT ====================

    @Transactional(readOnly = true)
    public PageResponse<BookingResponse> getAllBookings(Pageable pageable) {
        Page<Booking> page = bookingRepository.findAll(pageable);
        return PageResponse.<BookingResponse>builder()
                .content(page.getContent().stream().map(this::mapToBookingResponse).collect(Collectors.toList()))
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public PageResponse<BookingResponse> getBookingsByStatus(String status, Pageable pageable) {
        Booking.BookingStatus bookingStatus = Booking.BookingStatus.valueOf(status);
        Page<Booking> page = bookingRepository.findAllByBookingStatus(bookingStatus, pageable);
        return PageResponse.<BookingResponse>builder()
                .content(page.getContent().stream().map(this::mapToBookingResponse).collect(Collectors.toList()))
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public PageResponse<BookingResponse> getBookingsByPaymentStatus(String status, Pageable pageable) {
        Booking.PaymentStatus paymentStatus = Booking.PaymentStatus.valueOf(status);
        Page<Booking> page = bookingRepository.findAllByPaymentStatus(paymentStatus, pageable);
        return PageResponse.<BookingResponse>builder()
                .content(page.getContent().stream().map(this::mapToBookingResponse).collect(Collectors.toList()))
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public BookingResponse getBookingByCode(String code) {
        Booking booking = bookingRepository.findByBookingCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "code", code));
        return mapToBookingResponse(booking);
    }

    @Transactional
    public BookingResponse refundBooking(String bookingCode) {
        Booking booking = bookingRepository.findByBookingCode(bookingCode)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "code", bookingCode));

        if (booking.getPaymentStatus() != Booking.PaymentStatus.paid) {
            throw new BadRequestException("Chỉ có thể hoàn tiền vé đã thanh toán");
        }

        booking.setPaymentStatus(Booking.PaymentStatus.refunded);
        booking.setBookingStatus(Booking.BookingStatus.cancelled);
        Booking saved = bookingRepository.save(booking);
        log.info("Refunded booking: {}", bookingCode);
        return mapToBookingResponse(saved);
    }

    // ==================== PROVINCE MANAGEMENT ====================

    @Transactional(readOnly = true)
    public List<ProvinceResponse> getAllProvinces() {
        return provinceRepository.findAll().stream()
                .map(this::mapToProvinceResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProvinceResponse createProvince(ProvinceRequest request) {
        if (provinceRepository.findBySlug(request.getSlug()).isPresent()) {
            throw new BadRequestException("Slug đã tồn tại: " + request.getSlug());
        }
        if (provinceRepository.findByNameContainingIgnoreCase(request.getName()).stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(request.getName()))) {
            throw new BadRequestException("Tên tỉnh đã tồn tại: " + request.getName());
        }

        Province province = Province.builder()
                .name(request.getName())
                .slug(request.getSlug())
                .build();

        Province saved = provinceRepository.save(province);
        log.info("Created province: {}", saved.getName());
        return mapToProvinceResponse(saved);
    }

    @Transactional
    public ProvinceResponse updateProvince(Integer id, ProvinceRequest request) {
        Province province = provinceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Province", "id", id));

        if (request.getName() != null) province.setName(request.getName());
        if (request.getSlug() != null) province.setSlug(request.getSlug());

        Province saved = provinceRepository.save(province);
        log.info("Updated province: {}", id);
        return mapToProvinceResponse(saved);
    }

    @Transactional
    public void deleteProvince(Integer id) {
        Province province = provinceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Province", "id", id));

        boolean hasDepartureRoutes = province.getDepartureRoutes() != null && !province.getDepartureRoutes().isEmpty();
        boolean hasDestinationRoutes = province.getDestinationRoutes() != null && !province.getDestinationRoutes().isEmpty();

        if (hasDepartureRoutes || hasDestinationRoutes) {
            throw new BadRequestException("Không thể xóa tỉnh đang được sử dụng bởi các tuyến đường");
        }

        provinceRepository.delete(province);
        log.info("Deleted province: {}", id);
    }

    // ==================== MAPPERS ====================

    private UserManagementResponse mapToUserManagement(User u) {
        int totalBookings = u.getBookings() != null ? u.getBookings().size() : 0;
        int totalReviews = u.getReviews() != null ? u.getReviews().size() : 0;
        return UserManagementResponse.builder()
                .id(u.getId())
                .fullName(u.getFullName())
                .email(u.getEmail())
                .phone(u.getPhone())
                .avatarUrl(u.getAvatarUrl())
                .roleName(u.getRole().getRoleName())
                .isActive(u.getIsActive())
                .totalBookings(totalBookings)
                .totalReviews(totalReviews)
                .createdAt(u.getCreatedAt() != null ? u.getCreatedAt().toString() : null)
                .build();
    }

    private OperatorApprovalResponse mapToOperatorApproval(BusOperator o) {
        int totalTrips = o.getTrips() != null ? o.getTrips().size() : 0;
        int totalReviews = o.getReviews() != null ? o.getReviews().size() : 0;
        return OperatorApprovalResponse.builder()
                .id(o.getId())
                .name(o.getName())
                .phone(o.getPhone())
                .email(o.getEmail())
                .description(o.getDescription())
                .logoUrl(o.getLogoUrl())
                .isActive(o.getIsActive())
                .userId(o.getUserId())
                .totalTrips(totalTrips)
                .totalReviews(totalReviews)
                .avgRating(o.getAvgRating())
                .createdAt(o.getCreatedAt())
                .updatedAt(o.getUpdatedAt())
                .build();
    }

    private ProvinceResponse mapToProvinceResponse(Province p) {
        return ProvinceResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .slug(p.getSlug())
                .build();
    }

    private BookingResponse mapToBookingResponse(Booking b) {
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
}
