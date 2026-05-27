package com.parkingnow.payment.presentation;

import com.parkingnow.payment.application.PaymentService;
import com.parkingnow.payment.application.dto.PaymentRequest;
import com.parkingnow.payment.application.dto.PaymentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService service;

    @PostMapping("/demo")
    public ResponseEntity<PaymentResponse> demo(@Valid @RequestBody PaymentRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.demo(req));
    }

    @GetMapping("/reservation/{reservationId}")
    public PaymentResponse getByReservation(@PathVariable Long reservationId) {
        return service.findByReservation(reservationId);
    }

    @GetMapping
    public List<PaymentResponse> getAll() {
        return service.findAll();
    }
}
