package com.busapp.buss_api.controller;

import com.busapp.buss_api.dto.*;
import com.busapp.buss_api.service.RouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
@Tag(name = "Routes", description = "Tuyến đường")
public class RouteController {

    private final RouteService routeService;

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả tuyến đường")
    public ResponseEntity<ApiResponse<List<RouteResponse>>> getAll() {
        List<RouteResponse> routes = routeService.getAll();
        return ResponseEntity.ok(ApiResponse.success(routes));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy tuyến đường theo ID")
    public ResponseEntity<ApiResponse<RouteResponse>> getById(@PathVariable Integer id) {
        RouteResponse route = routeService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(route));
    }

    @GetMapping("/popular")
    @Operation(summary = "Lấy danh sách tuyến phổ biến")
    public ResponseEntity<ApiResponse<List<RouteResponse>>> getPopular() {
        List<RouteResponse> routes = routeService.getPopular();
        return ResponseEntity.ok(ApiResponse.success(routes));
    }

    @GetMapping("/search")
    @Operation(summary = "Tìm tuyến đường theo tỉnh")
    public ResponseEntity<ApiResponse<List<RouteResponse>>> search(
            @RequestParam String departure,
            @RequestParam String destination) {
        List<RouteResponse> routes = routeService.searchByProvinces(departure, destination);
        return ResponseEntity.ok(ApiResponse.success(routes));
    }

    @GetMapping("/{routeId}/pickup-dropoff-points")
    @Operation(summary = "Lấy điểm đón trả khách của tuyến")
    public ResponseEntity<ApiResponse<List<PickupDropoffPointResponse>>> getPickupDropoffPoints(
            @PathVariable Integer routeId) {
        List<PickupDropoffPointResponse> points = routeService.getPickupDropoffPoints(routeId);
        return ResponseEntity.ok(ApiResponse.success(points));
    }

    @PostMapping
    @Operation(summary = "Tạo tuyến đường mới")
    public ResponseEntity<ApiResponse<RouteResponse>> create(
            @Valid @RequestBody RouteRequest request) {
        RouteResponse route = routeService.create(request);
        return ResponseEntity.ok(ApiResponse.success(route, "Tạo tuyến đường thành công"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật tuyến đường")
    public ResponseEntity<ApiResponse<RouteResponse>> update(
            @PathVariable Integer id,
            @Valid @RequestBody RouteRequest request) {
        RouteResponse route = routeService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(route, "Cập nhật tuyến đường thành công"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa tuyến đường")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        routeService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa tuyến đường thành công"));
    }
}
