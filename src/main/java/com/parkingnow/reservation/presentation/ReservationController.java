package com.parkingnow.reservation.presentation;

import com.parkingnow.iam.infrastructure.UserRepository;
import com.parkingnow.reservation.application.ReservationService;
import com.parkingnow.reservation.application.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService service;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<ReservationResponse> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ReservationRequest req) {
        var user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(req, user.getId(), user.getEmail()));
    }

    @GetMapping("/my")
    public List<ReservationResponse> getMy(@AuthenticationPrincipal UserDetails userDetails) {
        var user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        return service.findByDriver(user.getId());
    }

    @GetMapping("/parking-lot/{lotId}")
    public List<ReservationResponse> getByLot(@PathVariable Long lotId) {
        return service.findByLot(lotId);
    }

    @PatchMapping("/{id}/cancel")
    public ReservationResponse cancel(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        var user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        return service.cancel(id, user.getId());
    }

    @PatchMapping("/{id}/consume")
    public ReservationResponse consume(@PathVariable Long id) {
        return service.consume(id);
    }
}
