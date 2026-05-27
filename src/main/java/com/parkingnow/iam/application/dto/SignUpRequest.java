package com.parkingnow.iam.application.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SignUpRequest {

    @NotBlank
    private String fullName;

    @Email @NotBlank
    private String email;

    @Size(min = 8, message = "Password must be at least 8 characters")
    @NotBlank
    private String password;

    private String phone;

    // Parking lot data
    @NotBlank
    private String lotName;

    @NotBlank
    private String lotAddress;

    @NotBlank
    private String lotDistrict;

    @Min(1)
    private int lotCapacity;

    @Pattern(regexp = "open|covered", message = "Lot type must be open or covered")
    private String lotType;
}
