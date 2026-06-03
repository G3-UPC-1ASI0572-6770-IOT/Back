package com.parkingnow.payment.infrastructure;

import com.parkingnow.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByReservationId(Long reservationId);

    List<Payment> findByDriverIdOrderByCreatedAtDesc(Long driverId);

    @Query("""
        SELECT p FROM Payment p
        JOIN com.parkingnow.reservation.domain.Reservation r ON p.reservationId = r.id
        WHERE r.lotId = :lotId
        ORDER BY p.createdAt DESC
        """)
    List<Payment> findByLotId(@Param("lotId") Long lotId);

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = 'PAID' AND p.paidAt >= :since")
    int countPaidSince(@Param("since") Instant since);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'PAID' AND p.paidAt >= :since")
    BigDecimal sumPaidSince(@Param("since") Instant since);

    @Query("""
        SELECT CAST(p.paidAt AS date), COALESCE(SUM(p.amount), 0)
        FROM Payment p
        WHERE p.status = 'PAID' AND p.paidAt >= :since
        GROUP BY CAST(p.paidAt AS date)
        ORDER BY CAST(p.paidAt AS date)
        """)
    List<Object[]> sumByDay(@Param("since") Instant since);
}
