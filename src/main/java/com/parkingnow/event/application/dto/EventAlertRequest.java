package com.parkingnow.event.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EventAlertRequest {
    @NotBlank private String severity;
    @NotBlank private String title;
    private String message;
    private Long lotId;
    private Long spaceId;
    private Long nodeId;
}
