package com.busapp.buss_api.controller;

import com.busapp.buss_api.dto.*;
import com.busapp.buss_api.service.VehicleTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicle-types")
@RequiredArgsConstructor
@Tag(name = "VehicleTypes", description = "Loại xe")
public class VehicleTypeController {

    private final VehicleTypeService vehicleTypeService;

    @GetMapping
    @Operation(summary = "Danh sách loại xe")
    public ResponseEntity<ApiResponse<List<VehicleTypeResponse>>> getAll() {
        List<VehicleTypeResponse> result = vehicleTypeService.getAll();
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Chi tiết loại xe")
    public ResponseEntity<ApiResponse<VehicleTypeResponse>> getById(@PathVariable Integer id) {
        VehicleTypeResponse response = vehicleTypeService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @Operation(summary = "Thêm loại xe mới")
    public ResponseEntity<ApiResponse<VehicleTypeResponse>> create(
            @Valid @RequestBody VehicleTypeRequest request) {
        VehicleTypeResponse response = vehicleTypeService.create(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Thêm loại xe thành công"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật loại xe")
    public ResponseEntity<ApiResponse<VehicleTypeResponse>> update(
            @PathVariable Integer id,
            @Valid @RequestBody VehicleTypeRequest request) {
        VehicleTypeResponse response = vehicleTypeService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Cập nhật loại xe thành công"));
    }
}
