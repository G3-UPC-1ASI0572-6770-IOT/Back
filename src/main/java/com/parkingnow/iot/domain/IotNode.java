package com.parkingnow.iot.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "iot_nodes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class IotNode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nodeCode;

    @Builder.Default
    private String firmware = "1.0.0";

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private NodeStatus status = NodeStatus.ONLINE;

    @Builder.Default
    private Instant lastSeen = Instant.now();

    private Long spaceId;
    private Long lotId;

    public enum NodeStatus { ONLINE, OFFLINE, WARNING }
}
