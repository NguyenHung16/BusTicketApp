package com.busapp.buss_api.repository;

import com.busapp.buss_api.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<User> findByRole_RoleName(String roleName, Pageable pageable);

    Page<User> findByFullNameContainingIgnoreCase(String keyword, Pageable pageable);

    Page<User> findByRole_RoleNameAndFullNameContainingIgnoreCase(String roleName, String keyword, Pageable pageable);

    long countByIsActiveTrue();

    long countByIsActiveFalse();
}
