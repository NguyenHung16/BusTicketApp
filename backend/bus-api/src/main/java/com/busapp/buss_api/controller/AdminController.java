package com.busapp.buss_api.controller;

import com.busapp.buss_api.dto.*;
import com.busapp.buss_api.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Quản trị hệ thống")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    // ==================== DASHBOARD ====================

    @GetMapping("/dashboard")
    @Operation(summary = "Lấy dữ liệu dashboard tổng quan")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard() {
        DashboardResponse dashboard = adminService.getDashboard();
        return ResponseEntity.ok(ApiResponse.success(dashboard));
    }

    // ==================== USER MANAGEMENT ====================

    @GetMapping("/users")
    @Operation(summary = "Lấy danh sách tất cả người dùng (phân trang)")
    public ResponseEntity<ApiResponse<PageResponse<UserManagementResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<UserManagementResponse> result = adminService.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/users/role/{role}")
    @Operation(summary = "Lấy danh sách người dùng theo vai trò (USER/OPERATOR/ADMIN)")
    public ResponseEntity<ApiResponse<PageResponse<UserManagementResponse>>> getUsersByRole(
            @PathVariable String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<UserManagementResponse> result = adminService.getUsersByRole(role, pageable);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Lấy chi tiết người dùng theo ID")
    public ResponseEntity<ApiResponse<UserManagementResponse>> getUserById(@PathVariable Integer id) {
        UserManagementResponse user = adminService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PostMapping("/users/{id}/lock")
    @Operation(summary = "Khóa tài khoản người dùng")
    public ResponseEntity<ApiResponse<UserManagementResponse>> lockUser(@PathVariable Integer id) {
        UserManagementResponse user = adminService.lockUser(id);
        return ResponseEntity.ok(ApiResponse.success(user, "Khóa tài khoản thành công"));
    }

    @PostMapping("/users/{id}/unlock")
    @Operation(summary = "Mở khóa tài khoản người dùng")
    public ResponseEntity<ApiResponse<UserManagementResponse>> unlockUser(@PathVariable Integer id) {
        UserManagementResponse user = adminService.unlockUser(id);
        return ResponseEntity.ok(ApiResponse.success(user, "Mở khóa tài khoản thành công"));
    }

    @PatchMapping("/users/{id}/role")
    @Operation(summary = "Thay đổi vai trò người dùng")
    public ResponseEntity<ApiResponse<UserManagementResponse>> changeUserRole(
            @PathVariable Integer id,
            @RequestParam String role) {
        UserManagementResponse user = adminService.changeUserRole(id, role);
        return ResponseEntity.ok(ApiResponse.success(user, "Thay đổi vai trò thành công"));
    }

    // ==================== OPERATOR MANAGEMENT ====================

    @GetMapping("/operators")
    @Operation(summary = "Lấy danh sách tất cả nhà xe (phân trang)")
    public ResponseEntity<ApiResponse<PageResponse<OperatorApprovalResponse>>> getAllOperators(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<OperatorApprovalResponse> result = adminService.getAllOperators(pageable);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/operators/pending")
    @Operation(summary = "Lấy danh sách nhà xe chờ duyệt")
    public ResponseEntity<ApiResponse<PageResponse<OperatorApprovalResponse>>> getPendingOperators(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<OperatorApprovalResponse> result = adminService.getPendingOperators(pageable);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/operators/active")
    @Operation(summary = "Lấy danh sách nhà xe đang hoạt động")
    public ResponseEntity<ApiResponse<PageResponse<OperatorApprovalResponse>>> getActiveOperators(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<OperatorApprovalResponse> result = adminService.getActiveOperators(pageable);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/operators/{id}")
    @Operation(summary = "Lấy chi tiết nhà xe theo ID")
    public ResponseEntity<ApiResponse<OperatorApprovalResponse>> getOperatorById(@PathVariable Integer id) {
        OperatorApprovalResponse operator = adminService.getOperatorById(id);
        return ResponseEntity.ok(ApiResponse.success(operator));
    }

    @PostMapping("/operators/{id}/approve")
    @Operation(summary = "Duyệt nhà xe")
    public ResponseEntity<ApiResponse<OperatorApprovalResponse>> approveOperator(@PathVariable Integer id) {
        OperatorApprovalResponse operator = adminService.approveOperator(id);
        return ResponseEntity.ok(ApiResponse.success(operator, "Duyệt nhà xe thành công"));
    }

    @PostMapping("/operators/{id}/reject")
    @Operation(summary = "Từ chối nhà xe")
    public ResponseEntity<ApiResponse<OperatorApprovalResponse>> rejectOperator(@PathVariable Integer id) {
        OperatorApprovalResponse operator = adminService.rejectOperator(id);
        return ResponseEntity.ok(ApiResponse.success(operator, "Từ chối nhà xe thành công"));
    }

    @PostMapping("/operators/{id}/deactivate")
    @Operation(summary = "Vô hiệu hóa nhà xe")
    public ResponseEntity<ApiResponse<OperatorApprovalResponse>> deactivateOperator(@PathVariable Integer id) {
        OperatorApprovalResponse operator = adminService.deactivateOperator(id);
        return ResponseEntity.ok(ApiResponse.success(operator, "Vô hiệu hóa nhà xe thành công"));
    }

    @PostMapping("/operators/{id}/activate")
    @Operation(summary = "Kích hoạt nhà xe")
    public ResponseEntity<ApiResponse<OperatorApprovalResponse>> activateOperator(@PathVariable Integer id) {
        OperatorApprovalResponse operator = adminService.activateOperator(id);
        return ResponseEntity.ok(ApiResponse.success(operator, "Kích hoạt nhà xe thành công"));
    }

    @GetMapping("/operators/top")
    @Operation(summary = "Lấy top nhà xe theo đánh giá")
    public ResponseEntity<ApiResponse<List<TopOperatorResponse>>> getTopOperators() {
        List<TopOperatorResponse> operators = adminService.getTopOperators();
        return ResponseEntity.ok(ApiResponse.success(operators));
    }

    // ==================== TRIP MANAGEMENT ====================

    @GetMapping("/trips")
    @Operation(summary = "Lấy danh sách tất cả chuyến xe (phân trang)")
    public ResponseEntity<ApiResponse<PageResponse<TripResponse>>> getAllTrips(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<TripResponse> result = adminService.getAllTrips(pageable);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // ==================== BOOKING MANAGEMENT ====================

    @GetMapping("/bookings")
    @Operation(summary = "Lấy danh sách tất cả đặt vé (phân trang)")
    public ResponseEntity<ApiResponse<PageResponse<BookingResponse>>> getAllBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<BookingResponse> result = adminService.getAllBookings(pageable);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/bookings/status/{status}")
    @Operation(summary = "Lấy danh sách vé theo trạng thái đặt (pending/confirmed/completed/cancelled)")
    public ResponseEntity<ApiResponse<PageResponse<BookingResponse>>> getBookingsByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<BookingResponse> result = adminService.getBookingsByStatus(status, pageable);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/bookings/payment/{status}")
    @Operation(summary = "Lấy danh sách vé theo trạng thái thanh toán (pending/paid/refunded)")
    public ResponseEntity<ApiResponse<PageResponse<BookingResponse>>> getBookingsByPaymentStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<BookingResponse> result = adminService.getBookingsByPaymentStatus(status, pageable);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/bookings/code/{code}")
    @Operation(summary = "Lấy chi tiết đặt vé theo mã")
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingByCode(@PathVariable String code) {
        BookingResponse booking = adminService.getBookingByCode(code);
        return ResponseEntity.ok(ApiResponse.success(booking));
    }

    @PostMapping("/bookings/{code}/refund")
    @Operation(summary = "Hoàn tiền đặt vé")
    public ResponseEntity<ApiResponse<BookingResponse>> refundBooking(@PathVariable String code) {
        BookingResponse booking = adminService.refundBooking(code);
        return ResponseEntity.ok(ApiResponse.success(booking, "Hoàn tiền thành công"));
    }

    // ==================== PROVINCE MANAGEMENT ====================

    @GetMapping("/provinces")
    @Operation(summary = "Lấy danh sách tất cả tỉnh thành")
    public ResponseEntity<ApiResponse<List<ProvinceResponse>>> getAllProvinces() {
        List<ProvinceResponse> provinces = adminService.getAllProvinces();
        return ResponseEntity.ok(ApiResponse.success(provinces));
    }

    @PostMapping("/provinces")
    @Operation(summary = "Tạo tỉnh thành mới")
    public ResponseEntity<ApiResponse<ProvinceResponse>> createProvince(
            @Valid @RequestBody ProvinceRequest request) {
        ProvinceResponse province = adminService.createProvince(request);
        return ResponseEntity.ok(ApiResponse.success(province, "Tạo tỉnh thành thành công"));
    }

    @PutMapping("/provinces/{id}")
    @Operation(summary = "Cập nhật tỉnh thành")
    public ResponseEntity<ApiResponse<ProvinceResponse>> updateProvince(
            @PathVariable Integer id,
            @Valid @RequestBody ProvinceRequest request) {
        ProvinceResponse province = adminService.updateProvince(id, request);
        return ResponseEntity.ok(ApiResponse.success(province, "Cập nhật tỉnh thành thành công"));
    }

    @DeleteMapping("/provinces/{id}")
    @Operation(summary = "Xóa tỉnh thành")
    public ResponseEntity<ApiResponse<Void>> deleteProvince(@PathVariable Integer id) {
        adminService.deleteProvince(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa tỉnh thành thành công"));
    }
}
