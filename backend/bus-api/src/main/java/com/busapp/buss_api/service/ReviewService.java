package com.busapp.buss_api.service;

import com.busapp.buss_api.dto.ReviewRequest;
import com.busapp.buss_api.dto.ReviewResponse;
import com.busapp.buss_api.entity.BusOperator;
import com.busapp.buss_api.entity.Review;
import com.busapp.buss_api.entity.Trip;
import com.busapp.buss_api.entity.User;
import com.busapp.buss_api.exception.BadRequestException;
import com.busapp.buss_api.exception.ResourceNotFoundException;
import com.busapp.buss_api.mapper.ResponseMapper;
import com.busapp.buss_api.repository.BusOperatorRepository;
import com.busapp.buss_api.repository.ReviewRepository;
import com.busapp.buss_api.repository.TripRepository;
import com.busapp.buss_api.repository.UserRepository;
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
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final TripRepository tripRepository;
    private final BusOperatorRepository busOperatorRepository;
    private final UserRepository userRepository;
    private final ResponseMapper mapper;

    @Transactional
    public ReviewResponse createReview(Integer userId, ReviewRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Trip trip = tripRepository.findById(request.getTripId())
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", request.getTripId()));

        BusOperator operator;
        if (request.getOperatorId() != null) {
            operator = busOperatorRepository.findById(request.getOperatorId())
                    .orElseThrow(() -> new ResourceNotFoundException("BusOperator", "id", request.getOperatorId()));
        } else {
            operator = trip.getOperator();
        }

        if (reviewRepository.existsByUserIdAndTripId(userId, request.getTripId())) {
            throw new BadRequestException("Bạn đã đánh giá chuyến đi này rồi");
        }

        Review review = Review.builder()
                .user(user)
                .trip(trip)
                .operator(operator)
                .rating(request.getRating())
                .comment(request.getComment())
                .isVerified(true)
                .build();

        Review savedReview = reviewRepository.save(review);
        log.info("Created review {} for trip {} by user {}", savedReview.getId(), request.getTripId(), userId);

        updateOperatorRating(operator.getId());

        return mapper.toReviewResponse(savedReview);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewsByOperator(Integer operatorId, Pageable pageable) {
        if (!busOperatorRepository.existsById(operatorId)) {
            throw new ResourceNotFoundException("BusOperator", "id", operatorId);
        }

        Page<Review> reviews = reviewRepository.findByOperatorId(operatorId, pageable);
        return reviews.map(mapper::toReviewResponse);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByTrip(Integer tripId) {
        if (!tripRepository.existsById(tripId)) {
            throw new ResourceNotFoundException("Trip", "id", tripId);
        }

        List<Review> reviews = reviewRepository.findByTripId(tripId);
        return reviews.stream()
                .map(mapper::toReviewResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReviewResponse getReviewByUserAndTrip(Integer userId, Integer tripId) {
        Review review = reviewRepository.findByUserIdAndTripId(userId, tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "userId & tripId", userId + "/" + tripId));
        return mapper.toReviewResponse(review);
    }

    @Transactional
    public void deleteReview(Integer reviewId, Integer userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));

        if (!review.getUser().getId().equals(userId)) {
            throw new BadRequestException("Bạn không có quyền xóa đánh giá này");
        }

        Integer operatorId = review.getOperator().getId();
        reviewRepository.delete(review);
        log.info("Deleted review {} by user {}", reviewId, userId);

        updateOperatorRating(operatorId);
    }

    @Transactional(readOnly = true)
    public Float getAverageRating(Integer operatorId) {
        if (!busOperatorRepository.existsById(operatorId)) {
            throw new ResourceNotFoundException("BusOperator", "id", operatorId);
        }

        Float avg = reviewRepository.calculateAverageRating(operatorId);
        return avg != null ? avg : 0.0f;
    }

    private void updateOperatorRating(Integer operatorId) {
        BusOperator operator = busOperatorRepository.findById(operatorId)
                .orElseThrow(() -> new ResourceNotFoundException("BusOperator", "id", operatorId));

        Float avg = reviewRepository.calculateAverageRating(operatorId);
        Integer count = reviewRepository.countByOperator(operatorId);

        operator.setAvgRating(avg != null ? avg : 0.0f);
        operator.setTotalReviews(count);

        busOperatorRepository.save(operator);
        log.info("Updated operator {} avgRating to {}, totalReviews to {}", operatorId, avg, count);
    }
}
