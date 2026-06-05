package com.busapp.buss_api.controller;

import com.busapp.buss_api.dto.*;
import com.busapp.buss_api.service.PointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
@Tag(name = "Points", description = "Điểm đón/trả khách")
public class PointController {

    private final PointService pointService;

    @PostMapping
    @Operation(summary = "Thêm điểm đón/trả")
    public ResponseEntity<ApiResponse<PickupDropoffPointResponse>> create(
            @Valid @RequestBody PointRequest request) {
        PickupDropoffPointResponse response = pointService.create(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Thêm điểm thành công"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật điểm đón/trả")
    public ResponseEntity<ApiResponse<PickupDropoffPointResponse>> update(
            @PathVariable Integer id,
            @Valid @RequestBody PointRequest request) {
        PickupDropoffPointResponse response = pointService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Cập nhật điểm thành công"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa điểm đón/trả")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        pointService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa điểm thành công"));
    }
}
