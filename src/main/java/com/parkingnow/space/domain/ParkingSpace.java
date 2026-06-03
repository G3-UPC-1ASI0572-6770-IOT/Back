package com.parkingnow.space.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "parking_spaces", indexes = {
    @Index(name = "idx_space_lot_id",     columnList = "lot_id"),
    @Index(name = "idx_space_lot_status", columnList = "lot_id, consolidated_status"),
    @Index(name = "idx_space_code_lot",   columnList = "code, lot_id")
})
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
    @Column(name = "physical_status")
    @Builder.Default
    private PhysicalStatus physicalStatus = PhysicalStatus.AVAILABLE;

    @Enumerated(EnumType.STRING)
    @Column(name = "logical_status")
    @Builder.Default
    private LogicalStatus logicalStatus = LogicalStatus.AVAILABLE;

    @Enumerated(EnumType.STRING)
    @Column(name = "consolidated_status")
    @Builder.Default
    private ConsolidatedStatus consolidatedStatus = ConsolidatedStatus.AVAILABLE;

    @Column(name = "iot_node_id")
    private String iotNodeId;

    @Column(name = "last_seen_at")
    private java.time.Instant lastSeenAt;

    @Column(name = "lot_id", nullable = false)
    private Long lotId;

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private java.time.Instant createdAt = java.time.Instant.now();

    @Column(name = "updated_at")
    @Builder.Default
    private java.time.Instant updatedAt = java.time.Instant.now();

    public enum PhysicalStatus { AVAILABLE, OCCUPIED, UNKNOWN }
    public enum LogicalStatus  { AVAILABLE, RESERVED }
    public enum ConsolidatedStatus { AVAILABLE, RESERVED, OCCUPIED, OFFLINE }
}
