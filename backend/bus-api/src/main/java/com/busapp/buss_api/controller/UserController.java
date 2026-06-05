package com.busapp.buss_api.controller;

import com.busapp.buss_api.dto.*;
import com.busapp.buss_api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Quản lý người dùng (Admin)")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Danh sách người dùng (phân trang)")
    public ResponseEntity<ApiResponse<PageResponse<UserAdminResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<UserAdminResponse> result = userService.getAllUsers(pageable, role, keyword);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Chi tiết người dùng")
    public ResponseEntity<ApiResponse<UserAdminResponse>> getById(@PathVariable Integer id) {
        UserAdminResponse response = userService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật / khóa tài khoản người dùng")
    public ResponseEntity<ApiResponse<UserAdminResponse>> update(
            @PathVariable Integer id,
            @Valid @RequestBody UserRequest request) {
        UserAdminResponse response = userService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Cập nhật thành công"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Vô hiệu hóa tài khoản người dùng")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        userService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Vô hiệu hóa tài khoản thành công"));
    }
}
