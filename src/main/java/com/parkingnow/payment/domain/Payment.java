package com.parkingnow.payment.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payments", indexes = {
    @Index(name = "idx_pay_reservation_id", columnList = "reservation_id"),
    @Index(name = "idx_pay_status",         columnList = "status"),
    @Index(name = "idx_pay_paid_at",        columnList = "paid_at"),
    @Index(name = "idx_pay_status_paid_at", columnList = "status, paid_at")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reservation_id", nullable = false)
    private Long reservationId;

    @Column(name = "driver_id")
    private Long driverId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Builder.Default
    private String currency = "PEN";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PaymentMethod method = PaymentMethod.DEMO_CARD;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    public enum PaymentMethod { DEMO_CARD, YAPE_DEMO, CASH_DEMO }
    public enum PaymentStatus { PENDING, PAID, FAILED, REFUNDED }
}
