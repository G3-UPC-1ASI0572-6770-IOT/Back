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
    private BigDecimal hourlyRate = BigDecimal.valueOf(3.00);

    @Builder.Default
    private String lotType = "open";

    private Long ownerId;

    private Double latitude;

    private Double longitude;

    private String nodeId;

    @Column(updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();
}
