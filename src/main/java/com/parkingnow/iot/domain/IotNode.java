package com.parkingnow.iot.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "iot_nodes", indexes = {
    @Index(name = "idx_node_lot_id",   columnList = "lot_id"),
    @Index(name = "idx_node_status",   columnList = "status"),
    @Index(name = "idx_node_last_seen", columnList = "last_seen")
})
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

    @Column(name = "last_seen")
    @Builder.Default
    private Instant lastSeen = Instant.now();

    private Long spaceId;

    @Column(name = "lot_id")
    private Long lotId;

    @Column(name = "api_key_hash")
    private String apiKeyHash;

    @Column(name = "last_heartbeat_at")
    private Instant lastHeartbeatAt;

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    @Builder.Default
    private Instant updatedAt = Instant.now();

    public enum NodeStatus { ONLINE, OFFLINE }
}
