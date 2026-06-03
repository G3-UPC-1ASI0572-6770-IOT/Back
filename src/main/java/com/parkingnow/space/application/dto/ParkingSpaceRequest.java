package com.parkingnow.space.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ParkingSpaceRequest {

    @NotNull
    private Long parkingLotId;

    @NotBlank
    private String label;

    private String zone;
    private String type;
    private String sensorCode;
}
