package com.parkingnow.iam.application;

import com.parkingnow.iam.application.dto.*;
import com.parkingnow.iam.domain.User;
import com.parkingnow.iam.infrastructure.UserRepository;
import com.parkingnow.parkinglot.application.ParkingLotService;
import com.parkingnow.parkinglot.application.dto.CreateParkingLotRequest;
import com.parkingnow.shared.exception.ConflictException;
import com.parkingnow.shared.exception.NotFoundException;
import com.parkingnow.shared.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtTokenProvider tokenProvider;
    private final ParkingLotService parkingLotService;

    // In-memory OTP store: email → {code, expiresAt}
    private final Map<String, OtpEntry> otpStore = new ConcurrentHashMap<>();

    record OtpEntry(String code, Instant expiresAt) {}

    @Transactional
    public AuthResponse signUp(SignUpRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new ConflictException("Email already registered: " + req.getEmail());
        }

        User user = User.builder()
                .fullName(req.getFullName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .phone(req.getPhone())
                .role("ADMIN")
                .build();
        user = userRepository.save(user);

        // Create the parking lot linked to this user
        var lotReq = new CreateParkingLotRequest();
        lotReq.setName(req.getLotName());
        lotReq.setAddress(req.getLotAddress());
        lotReq.setCity(req.getLotDistrict());
        lotReq.setCapacity(req.getLotCapacity());
        lotReq.setLotType(req.getLotType() != null ? req.getLotType() : "open");
        lotReq.setOwnerId(user.getId());
        var lot = parkingLotService.create(lotReq);

        String token = tokenProvider.generateTokenFromEmail(user.getEmail());
        return buildResponse(token, user, lot.getId());
    }

    @Transactional
    public AuthResponse signIn(SignInRequest req) {
        var auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
        User user = userRepository.findByEmail(req.getEmail()).orElseThrow();
        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        String token = tokenProvider.generateToken(auth);
        Long lotId = parkingLotService.findByOwnerId(user.getId())
                .map(l -> l.getId()).orElse(null);
        return buildResponse(token, user, lotId);
    }

    public void forgotPassword(ForgotPasswordRequest req) {
        // Always returns OK — don't reveal if email exists
        if (!userRepository.existsByEmail(req.getEmail())) return;

        String code = String.format("%06d", new Random().nextInt(999999));
        otpStore.put(req.getEmail(), new OtpEntry(code, Instant.now().plusSeconds(600)));

        // In production: send email. For dev: log it.
        System.out.println("[OTP] Code for " + req.getEmail() + " → " + code);
    }

    public boolean verifyCode(VerifyCodeRequest req) {
        var entry = otpStore.get(req.getEmail());
        if (entry == null) throw new IllegalArgumentException("No OTP requested for this email");
        if (Instant.now().isAfter(entry.expiresAt())) {
            otpStore.remove(req.getEmail());
            throw new IllegalArgumentException("OTP expired — request a new one");
        }
        return entry.code().equals(req.getCode());
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest req) {
        var verify = new VerifyCodeRequest();
        verify.setEmail(req.getEmail());
        verify.setCode(req.getCode());
        if (!verifyCode(verify)) {
            throw new IllegalArgumentException("Invalid verification code");
        }
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found"));
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
        otpStore.remove(req.getEmail());
    }

    public AuthResponse me(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found: " + email));
        Long lotId = parkingLotService.findByOwnerId(user.getId())
                .map(l -> l.getId()).orElse(null);
        String token = tokenProvider.generateTokenFromEmail(email);
        return buildResponse(token, user, lotId);
    }

    private AuthResponse buildResponse(String token, User user, Long lotId) {
        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .parkingLotId(lotId)
                .build();
    }
}
