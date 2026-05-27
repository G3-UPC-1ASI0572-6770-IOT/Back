package com.parkingnow.ticket.presentation;

import com.parkingnow.ticket.application.TicketService;
import com.parkingnow.ticket.application.dto.TicketResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService service;

    @PostMapping("/{reservationId}/generate")
    public ResponseEntity<TicketResponse> generate(@PathVariable Long reservationId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.generate(reservationId));
    }

    @GetMapping("/{reservationId}")
    public TicketResponse getByReservation(@PathVariable Long reservationId) {
        return service.findByReservation(reservationId);
    }
}
