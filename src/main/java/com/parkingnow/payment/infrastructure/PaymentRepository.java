package com.parkingnow.payment.infrastructure;

import com.parkingnow.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByReservationId(Long reservationId);
    List<Payment> findAllByOrderByCreatedAtDesc();

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = 'PAID' AND p.paidAt >= :since")
    int countPaidSince(Instant since);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'PAID' AND p.paidAt >= :since")
    java.math.BigDecimal sumPaidSince(Instant since);
}
