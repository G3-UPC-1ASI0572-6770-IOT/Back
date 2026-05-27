package com.parkingnow.iam.presentation;

import com.parkingnow.iam.application.AuthService;
import com.parkingnow.iam.application.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sign-up")
    public ResponseEntity<AuthResponse> signUp(@Valid @RequestBody SignUpRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signUp(req));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<AuthResponse> signIn(@Valid @RequestBody SignInRequest req) {
        return ResponseEntity.ok(authService.signIn(req));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest req) {
        authService.forgotPassword(req);
        return ResponseEntity.ok(Map.of("message", "If registered, a recovery code was sent."));
    }

    @PostMapping("/verify-code")
    public ResponseEntity<Map<String, Boolean>> verifyCode(@Valid @RequestBody VerifyCodeRequest req) {
        boolean valid = authService.verifyCode(req);
        return ResponseEntity.ok(Map.of("valid", valid));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest req) {
        authService.resetPassword(req);
        return ResponseEntity.ok(Map.of("message", "Password updated successfully."));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> me(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(authService.me(userDetails.getUsername()));
    }
}
