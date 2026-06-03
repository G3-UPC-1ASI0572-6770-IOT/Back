package com.parkingnow.iam.application;

import com.parkingnow.iam.application.dto.*;
import com.parkingnow.iam.domain.User;
import com.parkingnow.iam.infrastructure.UserRepository;
import com.parkingnow.shared.exception.ConflictException;
import com.parkingnow.shared.exception.NotFoundException;
import com.parkingnow.shared.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public AuthResponse signUpDriver(SignUpRequest req) {
        return signUp(req, "DRIVER");
    }

    @Transactional
    public AuthResponse signUpOwner(SignUpRequest req) {
        return signUp(req, "OWNER");
    }

    @Transactional
    public AuthResponse signIn(SignInRequest req) {
        var auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
        User user = userRepository.findByEmail(req.getEmail()).orElseThrow();
        user.setLastLoginAt(Instant.now());
        userRepository.save(user);
        String token = tokenProvider.generateToken(auth);
        return buildResponse(token, user);
    }

    @Transactional
    public void changePassword(String email, ChangePasswordRequest req) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword())) {
            throw new BadCredentialsException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
    }

    private AuthResponse signUp(SignUpRequest req, String role) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new ConflictException("Email already registered: " + req.getEmail());
        }
        User user = User.builder()
                .fullName(req.getName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(role)
                .build();
        user = userRepository.save(user);
        String token = tokenProvider.generateTokenFromEmail(user.getEmail());
        return buildResponse(token, user);
    }

    private AuthResponse buildResponse(String token, User user) {
        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }
}
