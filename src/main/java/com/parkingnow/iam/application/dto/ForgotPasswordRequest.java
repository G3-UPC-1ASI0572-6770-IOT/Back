package com.parkingnow.iam.application.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ForgotPasswordRequest {
    @Email @NotBlank
    private String email;
}
