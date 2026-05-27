package com.parkingnow.iam.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String phone;

    private String jobTitle;

    @Column(nullable = false)
    @Builder.Default
    private String role = "ADMIN";

    @Builder.Default
    private boolean active = true;

    @Builder.Default
    private boolean twoFactorEnabled = false;

    @Column(updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    private Instant lastLoginAt;

    // OTP para reset de contraseña (en memoria es suficiente en dev)
    @Transient
    private String resetOtp;
    @Transient
    private Instant otpExpiresAt;
}
