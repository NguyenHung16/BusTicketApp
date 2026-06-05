package com.busapp.buss_api.repository;

import com.busapp.buss_api.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    Page<Review> findByOperatorId(Integer operatorId, Pageable pageable);

    Optional<Review> findByUserIdAndTripId(Integer userId, Integer tripId);

    List<Review> findByTripId(Integer tripId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.operator.id = :operatorId")
    Float calculateAverageRating(@Param("operatorId") Integer operatorId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.operator.id = :operatorId")
    Integer countByOperator(@Param("operatorId") Integer operatorId);

    boolean existsByUserIdAndTripId(Integer userId, Integer tripId);
}
