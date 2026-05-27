package com.parkingnow.camera.application.dto;

import com.parkingnow.camera.domain.CameraFeed;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data @Builder
public class CameraFeedResponse {
    private Long id;
    private Long parkingLotId;
    private String nodeId;
    private String cameraUrl;
    private String status;
    private Instant lastSeenAt;
    private Instant createdAt;

    public static CameraFeedResponse from(CameraFeed c) {
        return CameraFeedResponse.builder()
                .id(c.getId())
                .parkingLotId(c.getParkingLotId())
                .nodeId(c.getNodeId())
                .cameraUrl(c.getCameraUrl())
                .status(c.getStatus().name())
                .lastSeenAt(c.getLastSeenAt())
                .createdAt(c.getCreatedAt())
                .build();
    }
}
