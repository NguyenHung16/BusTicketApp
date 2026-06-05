package com.busapp.buss_api.service;

import com.busapp.buss_api.dto.*;
import com.busapp.buss_api.entity.BusOperator;
import com.busapp.buss_api.exception.ResourceNotFoundException;
import com.busapp.buss_api.mapper.ResponseMapper;
import com.busapp.buss_api.repository.BusOperatorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OperatorService {

    private final BusOperatorRepository operatorRepository;
    private final ResponseMapper mapper;

    @Transactional(readOnly = true)
    public PageResponse<OperatorResponse> getAllOperators(Pageable pageable) {
        Page<BusOperator> page = operatorRepository.findByIsActiveTrue(pageable);
        return PageResponse.from(page, mapper::toOperatorResponse);
    }

    @Transactional(readOnly = true)
    public OperatorResponse getById(Integer id) {
        BusOperator operator = operatorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Operator", "id", id));
        return mapper.toOperatorResponse(operator);
    }

    @Transactional
    public OperatorResponse create(OperatorRequest request) {
        BusOperator operator = BusOperator.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .description(request.getDescription())
                .amenities(request.getAmenities())
                .cancellationPolicy(request.getCancellationPolicy())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        BusOperator saved = operatorRepository.save(operator);
        log.info("Created operator: {}", saved.getName());
        return mapper.toOperatorResponse(saved);
    }

    @Transactional
    public OperatorResponse update(Integer id, OperatorRequest request) {
        BusOperator operator = operatorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Operator", "id", id));

        if (request.getName() != null) operator.setName(request.getName());
        if (request.getPhone() != null) operator.setPhone(request.getPhone());
        if (request.getEmail() != null) operator.setEmail(request.getEmail());
        if (request.getDescription() != null) operator.setDescription(request.getDescription());
        if (request.getAmenities() != null) operator.setAmenities(request.getAmenities());
        if (request.getCancellationPolicy() != null) operator.setCancellationPolicy(request.getCancellationPolicy());
        if (request.getIsActive() != null) operator.setIsActive(request.getIsActive());

        BusOperator saved = operatorRepository.save(operator);
        log.info("Updated operator: {}", saved.getId());
        return mapper.toOperatorResponse(saved);
    }

    @Transactional
    public void delete(Integer id) {
        BusOperator operator = operatorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Operator", "id", id));
        operator.setIsActive(false);
        operatorRepository.save(operator);
        log.info("Deactivated operator: {}", id);
    }

    @Transactional(readOnly = true)
    public List<OperatorResponse> getTopRated() {
        return operatorRepository.findTopRated().stream()
                .map(mapper::toOperatorResponse)
                .collect(Collectors.toList());
    }
}
