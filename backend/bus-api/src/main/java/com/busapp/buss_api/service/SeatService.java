package com.busapp.buss_api.service;

import com.busapp.buss_api.dto.SeatMapResponse;
import com.busapp.buss_api.dto.SeatResponse;
import com.busapp.buss_api.entity.Seat;
import com.busapp.buss_api.entity.Trip;
import com.busapp.buss_api.entity.VehicleType;
import com.busapp.buss_api.exception.BadRequestException;
import com.busapp.buss_api.exception.ResourceNotFoundException;
import com.busapp.buss_api.mapper.ResponseMapper;
import com.busapp.buss_api.repository.SeatRepository;
import com.busapp.buss_api.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatService {

    private final SeatRepository seatRepository;
    private final TripRepository tripRepository;
    private final ResponseMapper mapper;

    @Transactional(readOnly = true)
    public SeatMapResponse getSeatsByTrip(Integer tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", tripId));

        VehicleType vehicleType = trip.getVehicleType();

        List<Seat> seats = seatRepository.findByTripIdOrderByFloorAscRowNumAscColNumAsc(tripId);
        List<SeatResponse> seatResponses = seats.stream()
                .map(mapper::toSeatResponse)
                .collect(Collectors.toList());

        Integer availableCount = seatRepository.countAvailableSeats(tripId);

        return SeatMapResponse.builder()
                .tripId(tripId)
                .vehicleTypeId(vehicleType.getId())
                .vehicleTypeName(vehicleType.getName())
                .seatLayout(vehicleType.getSeatLayout())
                .floorCount(vehicleType.getFloorCount())
                .totalSeats(seats.size())
                .availableSeats(availableCount)
                .seats(seatResponses)
                .build();
    }

    @Transactional(readOnly = true)
    public List<SeatResponse> getAvailableSeats(Integer tripId) {
        if (!tripRepository.existsById(tripId)) {
            throw new ResourceNotFoundException("Trip", "id", tripId);
        }

        List<Seat> seats = seatRepository.findByTripIdOrderByFloorAscRowNumAscColNumAsc(tripId);
        return seats.stream()
                .filter(seat -> seat.getStatus() == Seat.SeatStatus.available)
                .map(mapper::toSeatResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public SeatResponse lockSeat(Integer tripId, String seatCode, Integer userId) {
        Seat seat = seatRepository.findByTripIdAndSeatCode(tripId, seatCode)
                .orElseThrow(() -> new ResourceNotFoundException("Seat", "seatCode", seatCode));

        if (seat.getStatus() != Seat.SeatStatus.available) {
            throw new BadRequestException("Ghế " + seatCode + " không còn trống. Trạng thái hiện tại: " + seat.getStatus());
        }

        seat.setStatus(Seat.SeatStatus.locked);
        seat.setLockedBy(userId);
        Seat savedSeat = seatRepository.save(seat);
        log.info("User {} locked seat {} for trip {}", userId, seatCode, tripId);

        return mapper.toSeatResponse(savedSeat);
    }

    @Transactional
    public SeatResponse unlockSeat(Integer tripId, String seatCode, Integer userId) {
        Seat seat = seatRepository.findByTripIdAndSeatCode(tripId, seatCode)
                .orElseThrow(() -> new ResourceNotFoundException("Seat", "seatCode", seatCode));

        if (seat.getStatus() != Seat.SeatStatus.locked) {
            throw new BadRequestException("Ghế " + seatCode + " không ở trạng thái khóa. Trạng thái hiện tại: " + seat.getStatus());
        }

        if (seat.getLockedBy() != null && !seat.getLockedBy().equals(userId)) {
            throw new BadRequestException("Bạn không phải người khóa ghế này");
        }

        seat.setStatus(Seat.SeatStatus.available);
        seat.setLockedBy(null);
        Seat savedSeat = seatRepository.save(seat);
        log.info("User {} unlocked seat {} for trip {}", userId, seatCode, tripId);

        return mapper.toSeatResponse(savedSeat);
    }

    @Transactional
    public SeatResponse bookSeat(Integer tripId, String seatCode) {
        Seat seat = seatRepository.findByTripIdAndSeatCode(tripId, seatCode)
                .orElseThrow(() -> new ResourceNotFoundException("Seat", "seatCode", seatCode));

        if (seat.getStatus() == Seat.SeatStatus.booked) {
            throw new BadRequestException("Ghế " + seatCode + " đã được đặt trước đó");
        }
        if (seat.getStatus() != Seat.SeatStatus.available && seat.getStatus() != Seat.SeatStatus.locked) {
            throw new BadRequestException("Ghế " + seatCode + " không thể đặt. Trạng thái: " + seat.getStatus());
        }

        seat.setStatus(Seat.SeatStatus.booked);
        seatRepository.save(seat);

        Integer availableCount = seatRepository.countAvailableSeats(tripId);
        Trip trip = tripRepository.findById(tripId).orElse(null);
        if (trip != null) {
            trip.setAvailableSeats(availableCount);
            trip.setStatus(availableCount == 0 ? Trip.TripStatus.full : trip.getStatus());
            tripRepository.save(trip);
        }

        log.info("Booked seat {} for trip {}", seatCode, tripId);
        return mapper.toSeatResponse(seat);
    }

    @Transactional
    public void generateSeatsForTrip(Trip trip, VehicleType vt) {
        // Xóa ghế cũ nếu có
        seatRepository.deleteByTripId(trip.getId());

        int seatCount = vt.getSeatCount();
        int floorCount = vt.getFloorCount();
        int columns = parseSeatLayout(vt.getSeatLayout());
        
        List<Seat> seats = new ArrayList<>();
        int seatsPerFloor = (int) Math.ceil((double) seatCount / floorCount);
        
        for (int floor = 1; floor <= floorCount; floor++) {
            // Tầng 1 bắt đầu bằng A, Tầng 2 bắt đầu bằng B
            String prefix = (floor == 1) ? "A" : "B";
            
            int startIdx = (floor - 1) * seatsPerFloor;
            int endIdx = Math.min(floor * seatsPerFloor, seatCount);
            int currentFloorSeats = endIdx - startIdx;
            
            int rows = (int) Math.ceil((double) currentFloorSeats / columns);
            int seatInFloorCount = 0;

            for (int r = 1; r <= rows; r++) {
                for (int c = 1; c <= columns; c++) {
                    if (seatInFloorCount >= currentFloorSeats) break;
                    
                    seatInFloorCount++;
                    String seatCode = String.format("%s%02d", prefix, seatInFloorCount);

                    Seat seat = Seat.builder()
                            .trip(trip)
                            .seatCode(seatCode)
                            .floor(floor)
                            .rowNum(r)
                            .colNum(c)
                            .status(Seat.SeatStatus.available)
                            .build();

                    seats.add(seat);
                }
            }
        }

        seatRepository.saveAll(seats);
        // SỬA LỖI Ở ĐÂY: Đổi getTypeName() thành getName()
        log.info("Đã tạo {} ghế cho chuyến xe {} (Loại xe: {})", seats.size(), trip.getId(), vt.getName());
    }

    private int parseSeatLayout(String layout) {
        if (layout == null || layout.isEmpty()) {
            return 4; // Mặc định 4 cột
        }

        String[] parts = layout.split("-");
        int totalColumns = 0;
        for (String part : parts) {
            try {
                totalColumns += Integer.parseInt(part.trim());
            } catch (NumberFormatException e) {
                totalColumns = 4;
            }
        }
        return totalColumns > 0 ? totalColumns : 4;
    }
}
