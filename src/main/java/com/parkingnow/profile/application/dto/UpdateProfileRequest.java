package com.parkingnow.profile.application.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String fullName;
    private String phone;
    private String jobTitle;
}
