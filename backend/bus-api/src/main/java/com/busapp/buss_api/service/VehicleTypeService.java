package com.busapp.buss_api.service;

import com.busapp.buss_api.dto.VehicleTypeRequest;
import com.busapp.buss_api.dto.VehicleTypeResponse;
import com.busapp.buss_api.entity.VehicleType;
import com.busapp.buss_api.exception.ResourceNotFoundException;
import com.busapp.buss_api.repository.VehicleTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleTypeService {

    private final VehicleTypeRepository vehicleTypeRepository;

    @Transactional(readOnly = true)
    public List<VehicleTypeResponse> getAll() {
        return vehicleTypeRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public VehicleTypeResponse getById(Integer id) {
        VehicleType vt = vehicleTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VehicleType", "id", id));
        return mapToResponse(vt);
    }

    @Transactional
    public VehicleTypeResponse create(VehicleTypeRequest request) {
        VehicleType vt = VehicleType.builder()
                .name(request.getName())
                .seatCount(request.getSeatCount())
                .seatLayout(request.getSeatLayout())
                .floorCount(request.getFloorCount() != null ? request.getFloorCount() : 1)
                .description(request.getDescription())
                .build();

        VehicleType saved = vehicleTypeRepository.save(vt);
        log.info("Created vehicle type: {}", saved.getName());
        return mapToResponse(saved);
    }

    @Transactional
    public VehicleTypeResponse update(Integer id, VehicleTypeRequest request) {
        VehicleType vt = vehicleTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VehicleType", "id", id));

        if (request.getName() != null) vt.setName(request.getName());
        if (request.getSeatCount() != null) vt.setSeatCount(request.getSeatCount());
        if (request.getSeatLayout() != null) vt.setSeatLayout(request.getSeatLayout());
        if (request.getFloorCount() != null) vt.setFloorCount(request.getFloorCount());
        if (request.getDescription() != null) vt.setDescription(request.getDescription());

        VehicleType saved = vehicleTypeRepository.save(vt);
        log.info("Updated vehicle type: {}", id);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public VehicleType getEntityById(Integer id) {
        return vehicleTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VehicleType", "id", id));
    }

    private VehicleTypeResponse mapToResponse(VehicleType vt) {
        return VehicleTypeResponse.builder()
                .id(vt.getId())
                .name(vt.getName())
                .seatCount(vt.getSeatCount())
                .seatLayout(vt.getSeatLayout())
                .floorCount(vt.getFloorCount())
                .description(vt.getDescription())
                .build();
    }
}
