package com.busapp.buss_api.controller;

import com.busapp.buss_api.dto.*;
import com.busapp.buss_api.security.UserPrincipal;
import com.busapp.buss_api.service.SeatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
@Tag(name = "Seats", description = "Ghế xe")
public class SeatController {

    private final SeatService seatService;

    @GetMapping("/trip/{tripId}")
    @Operation(summary = "Lấy sơ đồ ghế của chuyến xe")
    public ResponseEntity<ApiResponse<SeatMapResponse>> getSeatMap(@PathVariable Integer tripId) {
        SeatMapResponse response = seatService.getSeatsByTrip(tripId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/trip/{tripId}/available")
    @Operation(summary = "Lấy danh sách ghế trống")
    public ResponseEntity<ApiResponse<List<SeatResponse>>> getAvailableSeats(@PathVariable Integer tripId) {
        List<SeatResponse> seats = seatService.getAvailableSeats(tripId);
        return ResponseEntity.ok(ApiResponse.success(seats));
    }

    @PostMapping("/lock/{tripId}/{seatCode}")
    @Operation(summary = "Khóa ghế (giữ chỗ tạm thời)")
    public ResponseEntity<ApiResponse<SeatResponse>> lockSeat(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Integer tripId,
            @PathVariable String seatCode) {
        SeatResponse response = seatService.lockSeat(tripId, seatCode, principal.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response, "Khóa ghế thành công"));
    }

    @DeleteMapping("/lock/{tripId}/{seatCode}")
    @Operation(summary = "Mở khóa ghế")
    public ResponseEntity<ApiResponse<SeatResponse>> unlockSeat(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Integer tripId,
            @PathVariable String seatCode) {
        SeatResponse response = seatService.unlockSeat(tripId, seatCode, principal.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response, "Mở khóa ghế thành công"));
    }
}
