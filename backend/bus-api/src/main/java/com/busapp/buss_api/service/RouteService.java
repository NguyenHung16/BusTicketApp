package com.busapp.buss_api.service;

import com.busapp.buss_api.dto.*;
import com.busapp.buss_api.entity.PickupDropoffPoint;
import com.busapp.buss_api.entity.Province;
import com.busapp.buss_api.entity.Route;
import com.busapp.buss_api.exception.BadRequestException;
import com.busapp.buss_api.exception.ResourceNotFoundException;
import com.busapp.buss_api.mapper.ResponseMapper;
import com.busapp.buss_api.repository.PickupDropoffPointRepository;
import com.busapp.buss_api.repository.ProvinceRepository;
import com.busapp.buss_api.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RouteService {

    private final RouteRepository routeRepository;
    private final ProvinceRepository provinceRepository;
    private final PickupDropoffPointRepository pointRepository;
    private final ResponseMapper mapper;

    @Transactional(readOnly = true)
    public List<RouteResponse> getAll() {
        return routeRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RouteResponse getById(Integer id) {
        Route route = routeRepository.findByIdWithProvinces(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", id));
        return mapToResponse(route);
    }

    @Transactional(readOnly = true)
    public List<RouteResponse> getPopular() {
        return routeRepository.findByIsPopularTrue().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RouteResponse> searchByProvinces(String departure, String destination) {
        return routeRepository.findByProvinceSlugs(departure, destination).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public RouteResponse create(RouteRequest request) {
        if (request.getDepartureProvinceId().equals(request.getDestinationProvinceId())) {
            throw new BadRequestException("Tỉnh đi và tỉnh đến không được trùng nhau");
        }

        if (routeRepository.findByDepartureProvinceIdAndDestinationProvinceId(
                request.getDepartureProvinceId(), request.getDestinationProvinceId()).isPresent()) {
            throw new BadRequestException("Tuyến đường đã tồn tại");
        }

        Province departure = provinceRepository.findById(request.getDepartureProvinceId())
                .orElseThrow(() -> new ResourceNotFoundException("Province", "id", request.getDepartureProvinceId()));
        Province destination = provinceRepository.findById(request.getDestinationProvinceId())
                .orElseThrow(() -> new ResourceNotFoundException("Province", "id", request.getDestinationProvinceId()));

        Route route = Route.builder()
                .departureProvince(departure)
                .destinationProvince(destination)
                .distanceKm(request.getDistanceKm())
                .durationHours(request.getDurationHours())
                .isPopular(Boolean.TRUE.equals(request.getIsPopular()))
                .build();

        Route saved = routeRepository.save(route);
        log.info("Created route: {} -> {}", departure.getName(), destination.getName());
        return mapToResponse(saved);
    }

    @Transactional
    public RouteResponse update(Integer id, RouteRequest request) {
        Route route = routeRepository.findByIdWithProvinces(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", id));

        if (request.getDepartureProvinceId() != null) {
            route.setDepartureProvince(findProvinceOrThrow(request.getDepartureProvinceId()));
        }
        if (request.getDestinationProvinceId() != null) {
            route.setDestinationProvince(findProvinceOrThrow(request.getDestinationProvinceId()));
        }
        if (request.getDistanceKm() != null) route.setDistanceKm(request.getDistanceKm());
        if (request.getDurationHours() != null) route.setDurationHours(request.getDurationHours());
        if (request.getIsPopular() != null) route.setIsPopular(request.getIsPopular());

        Route saved = routeRepository.save(route);
        return mapToResponse(saved);
    }

    @Transactional
    public void delete(Integer id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", id));
        routeRepository.delete(route);
        log.info("Deleted route: {}", id);
    }

    @Transactional(readOnly = true)
    public List<PickupDropoffPointResponse> getPickupDropoffPoints(Integer routeId) {
        if (!routeRepository.existsById(routeId)) {
            throw new ResourceNotFoundException("Route", "id", routeId);
        }
        return mapper.toPointResponseList(pointRepository.findByRouteId(routeId));
    }

    private Province findProvinceOrThrow(Integer id) {
        return provinceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Province", "id", id));
    }

    private RouteResponse mapToResponse(Route r) {
        return RouteResponse.builder()
                .id(r.getId())
                .departureProvinceId(r.getDepartureProvince().getId())
                .departureProvinceName(r.getDepartureProvince().getName())
                .departureProvinceSlug(r.getDepartureProvince().getSlug())
                .destinationProvinceId(r.getDestinationProvince().getId())
                .destinationProvinceName(r.getDestinationProvince().getName())
                .destinationProvinceSlug(r.getDestinationProvince().getSlug())
                .distanceKm(r.getDistanceKm())
                .durationHours(r.getDurationHours())
                .isPopular(r.getIsPopular())
                .build();
    }
}
