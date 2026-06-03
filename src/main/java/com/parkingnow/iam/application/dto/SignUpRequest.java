package com.parkingnow.iam.application.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SignUpRequest {

    @NotBlank
    private String name;

    @Email @NotBlank
    private String email;

    @Size(min = 6, message = "Password must be at least 6 characters")
    @NotBlank
    private String password;
}
