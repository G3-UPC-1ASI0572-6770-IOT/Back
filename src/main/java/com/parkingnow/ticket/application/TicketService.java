package com.parkingnow.ticket.application;

import com.parkingnow.reservation.infrastructure.ReservationRepository;
import com.parkingnow.shared.exception.ConflictException;
import com.parkingnow.shared.exception.NotFoundException;
import com.parkingnow.ticket.application.dto.TicketResponse;
import com.parkingnow.ticket.domain.Ticket;
import com.parkingnow.ticket.infrastructure.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepo;
    private final ReservationRepository reservationRepo;

    @Transactional
    public TicketResponse generate(Long reservationId) {
        if (!reservationRepo.existsById(reservationId)) {
            throw new NotFoundException("Reservation not found: " + reservationId);
        }
        if (ticketRepo.findByReservationId(reservationId).isPresent()) {
            throw new ConflictException("Ticket already generated for reservation: " + reservationId);
        }

        String code = "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String qrPayload = "PARKINGNOW|RES:" + reservationId + "|CODE:" + code;
        String qrUrl = "https://api.qrserver.com/v1/create-qr-code/?data=" +
                java.net.URLEncoder.encode(qrPayload, java.nio.charset.StandardCharsets.UTF_8) +
                "&size=200x200";

        Ticket ticket = Ticket.builder()
                .reservationId(reservationId)
                .ticketCode(code)
                .qrPayload(qrPayload)
                .qrUrl(qrUrl)
                .build();

        return TicketResponse.from(ticketRepo.save(ticket));
    }

    public TicketResponse findByReservation(Long reservationId) {
        return ticketRepo.findByReservationId(reservationId)
                .map(TicketResponse::from)
                .orElseThrow(() -> new NotFoundException("Ticket not found for reservation: " + reservationId));
    }
}
