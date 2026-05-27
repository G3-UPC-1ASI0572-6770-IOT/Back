package com.parkingnow.ticket.infrastructure;

import com.parkingnow.ticket.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByReservationId(Long reservationId);
    Optional<Ticket> findByTicketCode(String ticketCode);
}
