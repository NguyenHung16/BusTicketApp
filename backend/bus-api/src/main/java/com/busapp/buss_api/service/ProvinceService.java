package com.busapp.buss_api.service;

import com.busapp.buss_api.dto.PageResponse;
import com.busapp.buss_api.dto.ProvinceResponse;
import com.busapp.buss_api.entity.Province;
import com.busapp.buss_api.exception.ResourceNotFoundException;
import com.busapp.buss_api.repository.ProvinceRepository;
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
public class ProvinceService {

    private final ProvinceRepository provinceRepository;

    @Transactional(readOnly = true)
    public List<ProvinceResponse> getAllProvinces() {
        return provinceRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PageResponse<ProvinceResponse> getAllProvinces(Pageable pageable) {
        Page<Province> page = provinceRepository.findAll(pageable);
        return PageResponse.<ProvinceResponse>builder()
                .content(page.getContent().stream().map(this::mapToResponse).collect(Collectors.toList()))
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public ProvinceResponse getById(Integer id) {
        Province province = provinceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Province", "id", id));
        return mapToResponse(province);
    }

    @Transactional(readOnly = true)
    public ProvinceResponse getBySlug(String slug) {
        Province province = provinceRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Province", "slug", slug));
        return mapToResponse(province);
    }

    @Transactional(readOnly = true)
    public List<ProvinceResponse> searchByName(String query) {
        return provinceRepository.findByNameContainingIgnoreCase(query).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ProvinceResponse mapToResponse(Province p) {
        return ProvinceResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .slug(p.getSlug())
                .build();
    }
}
