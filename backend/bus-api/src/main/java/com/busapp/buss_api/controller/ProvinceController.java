package com.busapp.buss_api.controller;

import com.busapp.buss_api.dto.*;
import com.busapp.buss_api.service.ProvinceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/provinces")
@RequiredArgsConstructor
@Tag(name = "Provinces", description = "Tỉnh thành")
public class ProvinceController {

    private final ProvinceService provinceService;

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả tỉnh thành")
    public ResponseEntity<ApiResponse<List<ProvinceResponse>>> getAll() {
        List<ProvinceResponse> provinces = provinceService.getAllProvinces();
        return ResponseEntity.ok(ApiResponse.success(provinces));
    }

    @GetMapping("/paged")
    @Operation(summary = "Lấy danh sách tỉnh thành (phân trang)")
    public ResponseEntity<ApiResponse<PageResponse<ProvinceResponse>>> getAllPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<ProvinceResponse> result = provinceService.getAllProvinces(pageable);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy tỉnh theo ID")
    public ResponseEntity<ApiResponse<ProvinceResponse>> getById(@PathVariable Integer id) {
        ProvinceResponse response = provinceService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Lấy tỉnh theo slug")
    public ResponseEntity<ApiResponse<ProvinceResponse>> getBySlug(@PathVariable String slug) {
        ProvinceResponse response = provinceService.getBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/search")
    @Operation(summary = "Tìm kiếm tỉnh theo tên")
    public ResponseEntity<ApiResponse<List<ProvinceResponse>>> search(@RequestParam String q) {
        List<ProvinceResponse> results = provinceService.searchByName(q);
        return ResponseEntity.ok(ApiResponse.success(results));
    }
}
