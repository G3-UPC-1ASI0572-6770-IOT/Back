package com.parkingnow.reservation.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.parkingnow.reservation.domain.Reservation;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data @Builder
public class ReservationResponse {
    private Long id;
    private Long spaceId;
    private String spaceLabel;

    // Expose as both lotId AND parkingLotId so Flutter/Angular clients work with either
    private Long lotId;
    @JsonProperty("parkingLotId")
    private Long parkingLotId;

    private String parkingLotName;
    private Long driverId;
    private String driverEmail;
    private String status;
    private Instant createdAt;
    private Instant expiresAt;
    private Instant consumedAt;
    private Instant cancelledAt;
    private BigDecimal amount;

    public static ReservationResponse from(Reservation r) {
        return ReservationResponse.builder()
                .id(r.getId())
                .spaceId(r.getSpaceId())
                .spaceLabel(r.getSpaceLabel())
                .lotId(r.getLotId())
                .parkingLotId(r.getLotId())
                .parkingLotName(r.getParkingLotName())
                .driverId(r.getDriverId())
                .driverEmail(r.getDriverEmail())
                .status(r.getStatus().name())
                .createdAt(r.getCreatedAt())
                .expiresAt(r.getExpiresAt())
                .consumedAt(r.getConsumedAt())
                .cancelledAt(r.getCancelledAt())
                .amount(new BigDecimal("3.00"))
                .build();
    }
}
