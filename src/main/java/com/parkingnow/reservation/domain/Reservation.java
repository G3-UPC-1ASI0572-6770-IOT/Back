package com.parkingnow.reservation.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalTime;

@Entity
@Table(name = "reservations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String driverName;

    private String driverPhone;

    @Column(nullable = false)
    private Long spaceId;

    @Column(nullable = false)
    private Long lotId;

    private LocalTime startTime;
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ReservationStatus status = ReservationStatus.ACTIVE;

    @Builder.Default
    private Instant createdAt = Instant.now();

    public enum ReservationStatus { PENDING_PAYMENT, ACTIVE, EXPIRING, FINISHED, CONSUMED, CANCELLED, EXPIRED }
}
