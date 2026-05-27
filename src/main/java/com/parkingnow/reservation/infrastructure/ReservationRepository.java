package com.parkingnow.reservation.infrastructure;

import com.parkingnow.reservation.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByCode(String code);
    List<Reservation> findByLotId(Long lotId);
    List<Reservation> findBySpaceId(Long spaceId);
    List<Reservation> findByStatus(Reservation.ReservationStatus status);
    int countByLotIdAndStatus(Long lotId, Reservation.ReservationStatus status);
}
