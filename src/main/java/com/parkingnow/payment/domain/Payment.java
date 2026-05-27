package com.parkingnow.payment.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long reservationId;

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

    private Instant paidAt;

    @Column(updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    public enum PaymentMethod { DEMO_CARD, YAPE_DEMO, CASH_DEMO }
    public enum PaymentStatus { PENDING, PAID, FAILED, REFUNDED }
}
