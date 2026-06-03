package com.parkingnow.reservation.infrastructure;

import com.parkingnow.reservation.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByDriverId(Long driverId);

    List<Reservation> findByLotId(Long lotId);

    List<Reservation> findBySpaceId(Long spaceId);

    List<Reservation> findByStatus(Reservation.ReservationStatus status);

    int countByLotIdAndStatus(Long lotId, Reservation.ReservationStatus status);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.spaceId = :spaceId AND r.status = 'ACTIVE'")
    int countActiveBySpaceId(@Param("spaceId") Long spaceId);

    @Query("SELECT r FROM Reservation r WHERE r.status = 'ACTIVE' AND r.expiresAt < :now")
    List<Reservation> findExpired(@Param("now") Instant now);
}
