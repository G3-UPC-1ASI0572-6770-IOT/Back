package com.parkingnow.space.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "parking_spaces")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ParkingSpace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String code;

    private String zone;

    @Builder.Default
    private String type = "Standard";

    private String sensorCode;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SpaceStatus status = SpaceStatus.AVAILABLE;

    @Column(nullable = false)
    private Long lotId;

    public enum SpaceStatus { AVAILABLE, OCCUPIED, RESERVED, OFFLINE }
}
