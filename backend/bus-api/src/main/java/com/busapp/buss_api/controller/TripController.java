package com.busapp.buss_api.controller;

import com.busapp.buss_api.dto.*;
import com.busapp.buss_api.service.TripService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
@Tag(name = "Trips", description = "Chuyến xe")
public class TripController {

    private final TripService tripService;

    @GetMapping
    @Operation(summary = "Lấy tất cả danh sách chuyến xe")
    public ResponseEntity<ApiResponse<List<TripResponse>>> getAll() {
        List<TripResponse> trips = tripService.getAll();
        return ResponseEntity.ok(ApiResponse.success(trips));
    }

    @GetMapping("/search")
    @Operation(summary = "Tìm kiếm chuyến xe (theo tuyến, ngày, sắp xếp)")
    public ResponseEntity<ApiResponse<PageResponse<TripResponse>>> search(
            @RequestParam String departure,
            @RequestParam String destination,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "price") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Pageable pageable = PageRequest.of(page, size);
        TripSearchRequest request = TripSearchRequest.builder()
                .departure(departure)
                .destination(destination)
                .departureDate(departureDate)
                .sortBy(sortBy)
                .sortDir(sortDir)
                .build();
        PageResponse<TripResponse> trips = tripService.searchTrips(request, pageable);
        return ResponseEntity.ok(ApiResponse.success(trips));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin chuyến xe theo ID")
    public ResponseEntity<ApiResponse<TripResponse>> getById(@PathVariable Integer id) {
        TripResponse response = tripService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}/pickup-points")
    @Operation(summary = "Lấy điểm đón của chuyến xe")
    public ResponseEntity<ApiResponse<List<PickupDropoffPointResponse>>> getPickupPoints(@PathVariable Integer id) {
        List<PickupDropoffPointResponse> points = tripService.getPickupPoints(id);
        return ResponseEntity.ok(ApiResponse.success(points));
    }

    @GetMapping("/{id}/dropoff-points")
    @Operation(summary = "Lấy điểm trả của chuyến xe")
    public ResponseEntity<ApiResponse<List<PickupDropoffPointResponse>>> getDropoffPoints(@PathVariable Integer id) {
        List<PickupDropoffPointResponse> points = tripService.getDropoffPoints(id);
        return ResponseEntity.ok(ApiResponse.success(points));
    }

    @GetMapping("/operator/{operatorId}")
    @Operation(summary = "Lấy danh sách chuyến xe của nhà xe")
    public ResponseEntity<ApiResponse<List<TripResponse>>> getByOperator(
            @PathVariable Integer operatorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<TripResponse> trips = tripService.getByOperator(operatorId, date);
        return ResponseEntity.ok(ApiResponse.success(trips));
    }

    @PostMapping
    @Operation(summary = "Tạo chuyến xe mới")
    public ResponseEntity<ApiResponse<TripResponse>> create(
            @Valid @RequestBody TripRequest request) {
        TripResponse response = tripService.create(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Tạo chuyến xe thành công"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật chuyến xe")
    public ResponseEntity<ApiResponse<TripResponse>> update(
            @PathVariable Integer id,
            @Valid @RequestBody TripRequest request) {
        TripResponse response = tripService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Cập nhật chuyến xe thành công"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Hủy chuyến xe")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        tripService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Hủy chuyến xe thành công"));
    }
}
