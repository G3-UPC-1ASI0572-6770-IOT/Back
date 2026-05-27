package com.parkingnow.space.application.dto;

import com.parkingnow.space.domain.ParkingSpace;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class ParkingSpaceResponse {
    private Long id;
    private String code;
    private String zone;
    private String type;
    private String sensorCode;
    private String status;
    private Long lotId;

    public static ParkingSpaceResponse from(ParkingSpace s) {
        return ParkingSpaceResponse.builder()
                .id(s.getId())
                .code(s.getCode())
                .zone(s.getZone())
                .type(s.getType())
                .sensorCode(s.getSensorCode())
                .status(s.getStatus().name().toLowerCase())
                .lotId(s.getLotId())
                .build();
    }
}
