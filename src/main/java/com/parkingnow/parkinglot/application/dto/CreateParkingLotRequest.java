package com.parkingnow.parkinglot.application.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateParkingLotRequest {
    @NotBlank private String name;
    @NotBlank private String address;
    private String city;
    @Min(1) private int capacity;
    private BigDecimal hourlyRate;
    private String lotType;
    private Long ownerId;
}
