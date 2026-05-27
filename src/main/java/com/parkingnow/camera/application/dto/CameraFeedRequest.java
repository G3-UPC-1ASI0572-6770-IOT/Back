package com.parkingnow.camera.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CameraFeedRequest {
    @NotNull
    private Long parkingLotId;
    private String nodeId;
    @NotNull
    private String cameraUrl;
    private String status;  // ONLINE | OFFLINE
}
