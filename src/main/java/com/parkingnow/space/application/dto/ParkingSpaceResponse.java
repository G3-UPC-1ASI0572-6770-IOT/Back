package com.parkingnow.space.application.dto;

import com.parkingnow.space.domain.ParkingSpace;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data @Builder
public class ParkingSpaceResponse {
    private Long id;
    private String label;
    private String status;
    private String source;
    private Long lotId;
    private Instant lastUpdated;

    public static ParkingSpaceResponse from(ParkingSpace s) {
        return ParkingSpaceResponse.builder()
                .id(s.getId())
                .label(s.getCode())
                .status(mapStatus(s.getConsolidatedStatus()))
                .source(s.getIotNodeId() != null ? "SENSOR" : "MANUAL")
                .lotId(s.getLotId())
                .lastUpdated(s.getUpdatedAt())
                .build();
    }

    private static String mapStatus(ParkingSpace.ConsolidatedStatus cs) {
        if (cs == null) return "FREE";
        return switch (cs) {
            case AVAILABLE -> "FREE";
            case RESERVED  -> "RESERVED";
            case OCCUPIED  -> "OCCUPIED";
            case OFFLINE   -> "FREE";
        };
    }
}
