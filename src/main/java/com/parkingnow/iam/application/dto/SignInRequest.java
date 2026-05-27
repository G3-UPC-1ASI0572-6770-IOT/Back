package com.parkingnow.iam.application.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SignInRequest {
    @Email @NotBlank
    private String email;

    @NotBlank
    private String password;
}
