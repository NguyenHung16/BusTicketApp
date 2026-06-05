package com.busapp.buss_api.service;

import com.busapp.buss_api.dto.*;
import com.busapp.buss_api.entity.Role;
import com.busapp.buss_api.entity.User;
import com.busapp.buss_api.exception.BadRequestException;
import com.busapp.buss_api.exception.ResourceNotFoundException;
import com.busapp.buss_api.repository.RoleRepository;
import com.busapp.buss_api.repository.UserRepository;
import com.busapp.buss_api.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email đã được sử dụng");
        }

        Role customerRole = roleRepository.findByRoleName("customer")
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", "customer"));

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(customerRole)
                .isActive(true)
                .build();

        User saved = userRepository.save(user);
        log.info("Registered new user: {}", saved.getEmail());

        String token = jwtTokenProvider.generateToken(
                saved.getEmail(),
                saved.getRole().getRoleName(),
                saved.getId()
        );

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(saved.getId())
                .fullName(saved.getFullName())
                .email(saved.getEmail())
                .role(saved.getRole().getRoleName())
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Email hoặc mật khẩu không đúng"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Email hoặc mật khẩu không đúng");
        }

        if (!user.getIsActive()) {
            throw new BadRequestException("Tài khoản đã bị vô hiệu hóa");
        }

        log.info("User logged in: {}", user.getEmail());

        String token = jwtTokenProvider.generateToken(
                user.getEmail(),
                user.getRole().getRoleName(),
                user.getId()
        );

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().getRoleName())
                .build();
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole().getRoleName())
                .isActive(user.getIsActive())
                .build();
    }

    @Transactional
    public UserResponse updateCurrentUser(Integer userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getAvatarUrl() != null) user.setAvatarUrl(request.getAvatarUrl());

        User saved = userRepository.save(user);
        log.info("Updated profile for user: {}", userId);
        return UserResponse.builder()
                .id(saved.getId())
                .fullName(saved.getFullName())
                .email(saved.getEmail())
                .phone(saved.getPhone())
                .avatarUrl(saved.getAvatarUrl())
                .role(saved.getRole().getRoleName())
                .isActive(saved.getIsActive())
                .build();
    }
}
