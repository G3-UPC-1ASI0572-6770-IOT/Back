package com.parkingnow.iot.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "iot_events", indexes = {
    @Index(name = "idx_iot_event_node_id", columnList = "node_id"),
    @Index(name = "idx_iot_event_space", columnList = "parking_space_id"),
    @Index(name = "idx_iot_event_received", columnList = "received_at"),
    @Index(name = "idx_iot_event_status", columnList = "detected_status")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class IotEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nodeId;

    @Column(name = "parking_space_id")
    private Long parkingSpaceId;

    @Column(nullable = false)
    private String spaceCode;

    private Double distanceCm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DetectedStatus detectedStatus;

    @Column(nullable = false)
    @Builder.Default
    private Instant receivedAt = Instant.now();

    private Instant syncedAt;

    @Column(columnDefinition = "TEXT")
    private String rawPayload;

    @Column(updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    public enum DetectedStatus { AVAILABLE, OCCUPIED }
}
