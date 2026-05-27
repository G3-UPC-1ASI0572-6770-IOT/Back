package com.parkingnow.space.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ParkingSpaceRequest {
    @NotBlank private String code;
    private String zone;
    private String type;
    private String sensorCode;
    private String status;
}
