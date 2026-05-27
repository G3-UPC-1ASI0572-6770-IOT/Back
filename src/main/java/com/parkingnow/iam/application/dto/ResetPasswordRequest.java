package com.parkingnow.iam.application.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @Email @NotBlank
    private String email;

    @NotBlank @Size(min = 6, max = 6)
    private String code;

    @Size(min = 8)
    @NotBlank
    private String newPassword;
}
