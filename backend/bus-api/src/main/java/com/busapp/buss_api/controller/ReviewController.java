package com.busapp.buss_api.controller;

import com.busapp.buss_api.dto.*;
import com.busapp.buss_api.service.ReviewService;
import com.busapp.buss_api.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Đánh giá")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @Operation(summary = "Tạo đánh giá cho chuyến xe")
    public ResponseEntity<ApiResponse<ReviewResponse>> create(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ReviewRequest request) {
        ReviewResponse response = reviewService.createReview(principal.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success(response, "Đánh giá thành công"));
    }

    @GetMapping("/trip/{tripId}")
    @Operation(summary = "Lấy đánh giá theo chuyến xe")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getByTrip(@PathVariable Integer tripId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByTrip(tripId);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }

    @GetMapping("/operator/{operatorId}")
    @Operation(summary = "Lấy đánh giá theo nhà xe (phân trang)")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> getByOperator(
            @PathVariable Integer operatorId,
            @RequestParam(defaultValue = "0") int pageNum,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(pageNum, size);
        Page<ReviewResponse> page = reviewService.getReviewsByOperator(operatorId, pageable);
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

    @DeleteMapping("/{reviewId}")
    @Operation(summary = "Xóa đánh giá")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Integer reviewId,
            @AuthenticationPrincipal UserPrincipal principal) {
        reviewService.deleteReview(reviewId, principal.getUserId());
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa đánh giá thành công"));
    }
}
