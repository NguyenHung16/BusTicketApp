package com.busapp.buss_api.controller;

import com.busapp.buss_api.dto.*;
import com.busapp.buss_api.service.OperatorService;
import com.busapp.buss_api.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/operators")
@RequiredArgsConstructor
@Tag(name = "Operators", description = "Nhà xe")
public class OperatorController {

    private final OperatorService operatorService;
    private final ReviewService reviewService;

    @GetMapping
    @Operation(summary = "Lấy danh sách nhà xe (phân trang)")
    public ResponseEntity<ApiResponse<PageResponse<OperatorResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<OperatorResponse> result = operatorService.getAllOperators(pageable);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/top-rated")
    @Operation(summary = "Lấy danh sách nhà xe đánh giá cao")
    public ResponseEntity<ApiResponse<List<OperatorResponse>>> getTopRated() {
        List<OperatorResponse> operators = operatorService.getTopRated();
        return ResponseEntity.ok(ApiResponse.success(operators));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin nhà xe theo ID")
    public ResponseEntity<ApiResponse<OperatorResponse>> getById(@PathVariable Integer id) {
        OperatorResponse response = operatorService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}/reviews")
    @Operation(summary = "Lấy đánh giá của nhà xe")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> getReviews(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "0") int pageNum,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(pageNum, size);
        Page<ReviewResponse> page = reviewService.getReviewsByOperator(id, pageable);
        PageResponse<ReviewResponse> reviews = PageResponse.<ReviewResponse>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }

    @PostMapping
    @Operation(summary = "Tạo nhà xe mới")
    public ResponseEntity<ApiResponse<OperatorResponse>> create(
            @Valid @RequestBody OperatorRequest request) {
        OperatorResponse response = operatorService.create(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Tạo nhà xe thành công"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật nhà xe")
    public ResponseEntity<ApiResponse<OperatorResponse>> update(
            @PathVariable Integer id,
            @Valid @RequestBody OperatorRequest request) {
        OperatorResponse response = operatorService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Cập nhật thành công"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa nhà xe (Admin only)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        operatorService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa nhà xe thành công"));
    }
}
