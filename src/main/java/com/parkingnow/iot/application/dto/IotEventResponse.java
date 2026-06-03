package com.parkingnow.iot.application.dto;

import com.parkingnow.iot.domain.IotEvent;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data @Builder
public class IotEventResponse {
    private Long id;
    private String nodeId;
    private Long parkingSpaceId;
    private String spaceCode;
    private Double distanceCm;
    private String detectedStatus;
    private Instant receivedAt;
    private Instant syncedAt;
    private String syncStatus;
    private String result;

    public static IotEventResponse from(IotEvent e) {
        String sync = e.getSyncedAt() != null ? "SYNCED" : "PENDING";
        String resultMsg = e.getDetectedStatus() == IotEvent.DetectedStatus.OCCUPIED
                ? "Space marked OCCUPIED" : "Space marked AVAILABLE";

        return IotEventResponse.builder()
                .id(e.getId())
                .nodeId(e.getNodeId())
                .parkingSpaceId(e.getParkingSpaceId())
                .spaceCode(e.getSpaceCode())
                .distanceCm(e.getDistanceCm())
                .detectedStatus(e.getDetectedStatus().name())
                .receivedAt(e.getReceivedAt())
                .syncedAt(e.getSyncedAt())
                .syncStatus(sync)
                .result(resultMsg)
                .build();
    }
}
