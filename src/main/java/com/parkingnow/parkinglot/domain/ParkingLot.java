package com.parkingnow.parkinglot.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "parking_lots")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ParkingLot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    private String city;

    @Column(nullable = false)
    private int capacity;

    @Builder.Default
    private BigDecimal hourlyRate = BigDecimal.valueOf(3.50);

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private LotStatus status = LotStatus.AVAILABLE;

    @Builder.Default
    private String lotType = "open";

    private Long ownerId;

    @Builder.Default
    private double rating = 4.5;

    @Column(updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    public enum LotStatus { AVAILABLE, OCCUPIED, MAINTENANCE }
}
