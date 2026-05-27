package com.parkingnow.event.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "event_alerts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EventAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severity;

    @Column(nullable = false)
    private String title;

    private String message;

    private Long lotId;
    private Long spaceId;
    private Long nodeId;

    @Builder.Default
    private Instant createdAt = Instant.now();

    public enum Severity { CRITICAL, WARNING, INFO, RESOLVED }
}
