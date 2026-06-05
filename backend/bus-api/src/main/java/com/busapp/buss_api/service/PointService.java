package com.busapp.buss_api.service;

import com.busapp.buss_api.dto.PointRequest;
import com.busapp.buss_api.dto.PickupDropoffPointResponse;
import com.busapp.buss_api.entity.BusOperator;
import com.busapp.buss_api.entity.PickupDropoffPoint;
import com.busapp.buss_api.entity.Route;
import com.busapp.buss_api.exception.ResourceNotFoundException;
import com.busapp.buss_api.repository.BusOperatorRepository;
import com.busapp.buss_api.repository.PickupDropoffPointRepository;
import com.busapp.buss_api.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointService {

    private final PickupDropoffPointRepository pointRepository;
    private final BusOperatorRepository operatorRepository;
    private final RouteRepository routeRepository;

    @Transactional
    public PickupDropoffPointResponse create(PointRequest request) {
        BusOperator operator = operatorRepository.findById(request.getOperatorId())
                .orElseThrow(() -> new ResourceNotFoundException("Operator", "id", request.getOperatorId()));
        Route route = routeRepository.findById(request.getRouteId())
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", request.getRouteId()));

        PickupDropoffPoint point = PickupDropoffPoint.builder()
                .operator(operator)
                .route(route)
                .pointType(PickupDropoffPoint.PointType.valueOf(request.getPointType()))
                .name(request.getName())
                .address(request.getAddress())
                .latitude(request.getLatitude() != null ? BigDecimal.valueOf(request.getLatitude()) : null)
                .longitude(request.getLongitude() != null ? BigDecimal.valueOf(request.getLongitude()) : null)
                .pickupTimeNote(request.getPickupTimeNote())
                .isActive(true)
                .build();

        PickupDropoffPoint saved = pointRepository.save(point);
        log.info("Created point: {} for route {}", saved.getName(), route.getId());
        return mapToResponse(saved);
    }

    @Transactional
    public PickupDropoffPointResponse update(Integer id, PointRequest request) {
        PickupDropoffPoint point = pointRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Point", "id", id));

        if (request.getOperatorId() != null) {
            BusOperator operator = operatorRepository.findById(request.getOperatorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Operator", "id", request.getOperatorId()));
            point.setOperator(operator);
        }
        if (request.getRouteId() != null) {
            Route route = routeRepository.findById(request.getRouteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Route", "id", request.getRouteId()));
            point.setRoute(route);
        }
        if (request.getPointType() != null) point.setPointType(PickupDropoffPoint.PointType.valueOf(request.getPointType()));
        if (request.getName() != null) point.setName(request.getName());
        if (request.getAddress() != null) point.setAddress(request.getAddress());
        if (request.getLatitude() != null) point.setLatitude(BigDecimal.valueOf(request.getLatitude()));
        if (request.getLongitude() != null) point.setLongitude(BigDecimal.valueOf(request.getLongitude()));
        if (request.getPickupTimeNote() != null) point.setPickupTimeNote(request.getPickupTimeNote());

        PickupDropoffPoint saved = pointRepository.save(point);
        log.info("Updated point: {}", id);
        return mapToResponse(saved);
    }

    @Transactional
    public void delete(Integer id) {
        PickupDropoffPoint point = pointRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Point", "id", id));
        point.setIsActive(false);
        pointRepository.save(point);
        log.info("Deactivated point: {}", id);
    }

    private PickupDropoffPointResponse mapToResponse(PickupDropoffPoint p) {
        return PickupDropoffPointResponse.builder()
                .id(p.getId())
                .operatorId(p.getOperator() != null ? p.getOperator().getId() : null)
                .routeId(p.getRoute() != null ? p.getRoute().getId() : null)
                .pointType(p.getPointType() != null ? p.getPointType().name() : null)
                .name(p.getName())
                .address(p.getAddress())
                .latitude(p.getLatitude())
                .longitude(p.getLongitude())
                .pickupTimeNote(p.getPickupTimeNote())
                .isActive(p.getIsActive())
                .build();
    }
}
