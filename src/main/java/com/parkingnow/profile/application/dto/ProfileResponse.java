package com.parkingnow.profile.application.dto;

import com.parkingnow.iam.domain.User;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data @Builder
public class ProfileResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String jobTitle;
    private String role;
    private String avatarInitials;
    private boolean twoFactorEnabled;
    private boolean active;
    private Instant createdAt;
    private Instant lastLoginAt;
    private Long parkingLotId;

    public static ProfileResponse from(User user, Long lotId) {
        String[] parts = user.getFullName().trim().split("\\s+");
        String initials = parts.length >= 2
                ? String.valueOf(parts[0].charAt(0)) + parts[parts.length - 1].charAt(0)
                : String.valueOf(parts[0].charAt(0));
        return ProfileResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .jobTitle(user.getJobTitle())
                .role(user.getRole())
                .avatarInitials(initials.toUpperCase())
                .twoFactorEnabled(user.isTwoFactorEnabled())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .parkingLotId(lotId)
                .build();
    }
}
