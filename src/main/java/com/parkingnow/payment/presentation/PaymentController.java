package com.parkingnow.payment.presentation;

import com.parkingnow.iam.infrastructure.UserRepository;
import com.parkingnow.payment.application.PaymentService;
import com.parkingnow.payment.application.dto.PaymentRequest;
import com.parkingnow.payment.application.dto.PaymentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService service;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<PaymentResponse> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PaymentRequest req) {
        var user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req, user.getId()));
    }

    @GetMapping("/my")
    public List<PaymentResponse> getMy(@AuthenticationPrincipal UserDetails userDetails) {
        var user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        return service.findByDriver(user.getId());
    }

    @GetMapping("/parking-lot/{lotId}")
    public List<PaymentResponse> getByLot(@PathVariable Long lotId) {
        return service.findByLot(lotId);
    }
}
