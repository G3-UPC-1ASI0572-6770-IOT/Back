package com.parkingnow.iot.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class IotEventRequest {

    @NotBlank
    private String nodeId;

    private String spaceLabel;

    private String spaceCode;

    private String status;

    private String detectedStatus;

    private Double distanceCm;

    private Long timestamp;

    private Long parkingSpaceId;

    public String resolvedSpaceLabel() {
        return spaceLabel != null ? spaceLabel : spaceCode;
    }

    public String resolvedStatus() {
        if (status != null) return status;
        if (detectedStatus != null) {
            return "AVAILABLE".equalsIgnoreCase(detectedStatus) ? "FREE" : detectedStatus;
        }
        return "FREE";
    }
}
