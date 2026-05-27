package com.parkingnow.ticket.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "tickets")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long reservationId;

    @Column(nullable = false, unique = true)
    private String ticketCode;

    @Column(nullable = false)
    private String qrPayload;

    private String qrUrl;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TicketStatus status = TicketStatus.ACTIVE;

    @Column(updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    public enum TicketStatus { ACTIVE, USED, EXPIRED }
}
