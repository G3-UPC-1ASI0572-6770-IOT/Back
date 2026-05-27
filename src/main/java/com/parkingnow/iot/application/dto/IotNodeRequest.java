package com.parkingnow.iot.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class IotNodeRequest {
    @NotBlank private String nodeCode;
    private String firmware;
    private Long spaceId;
    private Long lotId;
}
