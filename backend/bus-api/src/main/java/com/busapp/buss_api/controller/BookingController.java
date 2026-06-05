package com.busapp.buss_api.controller;

import com.busapp.buss_api.dto.*;
import com.busapp.buss_api.security.UserPrincipal;
import com.busapp.buss_api.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Tag(name = "Bookings", description = "Đặt vé")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @Operation(summary = "Tạo đặt vé mới")
    public ResponseEntity<ApiResponse<BookingResponse>> create(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody BookingRequest request) {
        BookingResponse response = bookingService.createBooking(principal.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success(response, "Đặt vé thành công. Mã vé: " + response.getBookingCode()));
    }

    @GetMapping("/my")
    @Operation(summary = "Lấy danh sách vé của tôi")
    public ResponseEntity<ApiResponse<PageResponse<BookingResponse>>> getMyBookings(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<BookingResponse> bookings = bookingService.getMyBookings(principal.getUserId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(bookings));
    }

    @GetMapping("/code/{bookingCode}")
    @Operation(summary = "Lấy thông tin đặt vé theo mã")
    public ResponseEntity<ApiResponse<BookingResponse>> getByCode(@PathVariable String bookingCode) {
        BookingResponse response = bookingService.getBookingByCode(bookingCode);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{bookingCode}/confirm")
    @Operation(summary = "Xác nhận đặt vé")
    public ResponseEntity<ApiResponse<BookingResponse>> confirm(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String bookingCode) {
        BookingResponse response = bookingService.confirmBooking(bookingCode, principal.getUserId(), principal.getRole());
        return ResponseEntity.ok(ApiResponse.success(response, "Xác nhận đặt vé thành công"));
    }

    @PostMapping("/{bookingCode}/cancel")
    @Operation(summary = "Hủy đặt vé")
    public ResponseEntity<ApiResponse<BookingResponse>> cancel(
            @PathVariable String bookingCode,
            @AuthenticationPrincipal UserPrincipal principal) {
        BookingResponse response = bookingService.cancelBooking(bookingCode, principal.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response, "Hủy đặt vé thành công"));
    }

    @PatchMapping("/{bookingCode}/payment")
    @Operation(summary = "Cập nhật trạng thái thanh toán")
    public ResponseEntity<ApiResponse<BookingResponse>> updatePayment(
            @PathVariable String bookingCode,
            @RequestParam String status) {
        BookingResponse response = bookingService.updatePaymentStatus(bookingCode, status);
        return ResponseEntity.ok(ApiResponse.success(response, "Cập nhật thanh toán thành công"));
    }
}
