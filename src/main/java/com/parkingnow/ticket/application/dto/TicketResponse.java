package com.parkingnow.ticket.application.dto;

import com.parkingnow.ticket.domain.Ticket;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data @Builder
public class TicketResponse {
    private Long id;
    private Long reservationId;
    private String ticketCode;
    private String qrPayload;
    private String qrUrl;
    private String status;
    private Instant createdAt;

    public static TicketResponse from(Ticket t) {
        return TicketResponse.builder()
                .id(t.getId())
                .reservationId(t.getReservationId())
                .ticketCode(t.getTicketCode())
                .qrPayload(t.getQrPayload())
                .qrUrl(t.getQrUrl())
                .status(t.getStatus().name())
                .createdAt(t.getCreatedAt())
                .build();
    }
}
