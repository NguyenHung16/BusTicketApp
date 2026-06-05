package com.busapp.buss_api.service;

import com.busapp.buss_api.dto.PageResponse;
import com.busapp.buss_api.dto.ProvinceResponse;
import com.busapp.buss_api.dto.UserAdminResponse;
import com.busapp.buss_api.dto.UserRequest;
import com.busapp.buss_api.entity.User;
import com.busapp.buss_api.exception.BadRequestException;
import com.busapp.buss_api.exception.ResourceNotFoundException;
import com.busapp.buss_api.mapper.ResponseMapper;
import com.busapp.buss_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final ResponseMapper mapper;

    @Transactional(readOnly = true)
    public PageResponse<UserAdminResponse> getAllUsers(Pageable pageable, String role, String keyword) {
        Page<User> page;

        if (role != null && !role.isEmpty() && keyword != null && !keyword.isEmpty()) {
            page = userRepository.findByRole_RoleNameAndFullNameContainingIgnoreCase(role, keyword, pageable);
        } else if (role != null && !role.isEmpty()) {
            page = userRepository.findByRole_RoleName(role, pageable);
        } else if (keyword != null && !keyword.isEmpty()) {
            page = userRepository.findByFullNameContainingIgnoreCase(keyword, pageable);
        } else {
            page = userRepository.findAll(pageable);
        }

        return PageResponse.from(page, mapper::toUserAdminResponse);
    }

    @Transactional(readOnly = true)
    public UserAdminResponse getById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return mapper.toUserAdminResponse(user);
    }

    @Transactional
    public UserAdminResponse update(Integer id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getAvatarUrl() != null) user.setAvatarUrl(request.getAvatarUrl());
        if (request.getIsActive() != null) user.setIsActive(request.getIsActive());

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new BadRequestException("Email đã được sử dụng");
            }
            user.setEmail(request.getEmail());
        }

        User saved = userRepository.save(user);
        log.info("Admin updated user: {}", id);
        return mapper.toUserAdminResponse(saved);
    }

    @Transactional
    public void delete(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        user.setIsActive(false);
        userRepository.save(user);
        log.info("Deactivated user: {}", id);
    }
}
