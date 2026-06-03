package com.parkingnow.reservation.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "reservations", indexes = {
    @Index(name = "idx_res_driver_id",  columnList = "driver_id"),
    @Index(name = "idx_res_space_id",   columnList = "space_id"),
    @Index(name = "idx_res_lot_id",     columnList = "lot_id"),
    @Index(name = "idx_res_status",     columnList = "status")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "space_id", nullable = false)
    private Long spaceId;

    @Column(name = "space_label")
    private String spaceLabel;

    @Column(name = "lot_id")
    private Long lotId;

    @Column(name = "parking_lot_name")
    private String parkingLotName;

    @Column(name = "driver_id")
    private Long driverId;

    @Column(name = "driver_email")
    private String driverEmail;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ReservationStatus status = ReservationStatus.ACTIVE;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "consumed_at")
    private Instant consumedAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    public enum ReservationStatus { ACTIVE, CANCELLED, EXPIRED, CONSUMED }
}
