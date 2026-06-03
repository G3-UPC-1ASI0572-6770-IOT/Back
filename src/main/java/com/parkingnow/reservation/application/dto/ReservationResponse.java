package com.parkingnow.reservation.application.dto;

import com.parkingnow.reservation.domain.Reservation;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data @Builder
public class ReservationResponse {
    private Long id;
    private Long spaceId;
    private String spaceLabel;
    private Long lotId;
    private String parkingLotName;
    private Long driverId;
    private String driverEmail;
    private String status;
    private Instant createdAt;
    private Instant expiresAt;
    private Instant consumedAt;
    private Instant cancelledAt;

    public static ReservationResponse from(Reservation r) {
        return ReservationResponse.builder()
                .id(r.getId())
                .spaceId(r.getSpaceId())
                .spaceLabel(r.getSpaceLabel())
                .lotId(r.getLotId())
                .parkingLotName(r.getParkingLotName())
                .driverId(r.getDriverId())
                .driverEmail(r.getDriverEmail())
                .status(r.getStatus().name())
                .createdAt(r.getCreatedAt())
                .expiresAt(r.getExpiresAt())
                .consumedAt(r.getConsumedAt())
                .cancelledAt(r.getCancelledAt())
                .build();
    }
}
