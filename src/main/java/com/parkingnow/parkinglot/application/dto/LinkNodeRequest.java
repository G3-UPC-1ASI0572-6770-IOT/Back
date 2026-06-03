package com.parkingnow.parkinglot.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LinkNodeRequest {
    @NotBlank
    private String nodeId;
}
