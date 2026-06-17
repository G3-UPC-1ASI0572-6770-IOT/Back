package com.parkingnow.camera.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "camera_feeds")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CameraFeed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long parkingLotId;

    private String nodeId;

    // Holds either an external URL or a base64 data URI (data:image/jpeg;base64,...)
    // from the ESP32-CAM snapshot, so it must be a large text column.
    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String cameraUrl;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CameraStatus status = CameraStatus.OFFLINE;

    @Builder.Default
    private Instant lastSeenAt = Instant.now();

    @Column(updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    public enum CameraStatus { ONLINE, OFFLINE }
}
