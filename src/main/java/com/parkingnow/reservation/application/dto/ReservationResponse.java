package com.parkingnow.reservation.application.dto;

import com.parkingnow.reservation.domain.Reservation;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data @Builder
public class ReservationResponse {
    private Long id;
    private String code;
    private String driverName;
    private String driverPhone;
    private Long spaceId;
    private Long lotId;
    private String startTime;
    private String endTime;
    private String status;
    private Instant createdAt;

    public static ReservationResponse from(Reservation r) {
        return ReservationResponse.builder()
                .id(r.getId())
                .code(r.getCode())
                .driverName(r.getDriverName())
                .driverPhone(r.getDriverPhone())
                .spaceId(r.getSpaceId())
                .lotId(r.getLotId())
                .startTime(r.getStartTime() != null ? r.getStartTime().toString() : null)
                .endTime(r.getEndTime() != null ? r.getEndTime().toString() : null)
                .status(r.getStatus().name().toLowerCase())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
