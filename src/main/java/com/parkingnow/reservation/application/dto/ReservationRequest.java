package com.parkingnow.reservation.application.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReservationRequest {
    @NotBlank private String driverName;
    private String driverPhone;
    @NotNull private Long spaceId;
    @NotNull private Long lotId;
    private String startTime;
    private String endTime;
}
