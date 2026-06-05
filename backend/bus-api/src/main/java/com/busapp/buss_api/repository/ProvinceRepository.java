package com.busapp.buss_api.repository;

import com.busapp.buss_api.entity.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProvinceRepository extends JpaRepository<Province, Integer> {

    Optional<Province> findBySlug(String slug);

    List<Province> findByNameContainingIgnoreCase(String name);
}
