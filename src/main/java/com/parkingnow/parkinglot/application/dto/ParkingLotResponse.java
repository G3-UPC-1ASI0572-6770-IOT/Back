package com.parkingnow.parkinglot.application.dto;

import com.parkingnow.parkinglot.domain.ParkingLot;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data @Builder
public class ParkingLotResponse {
    private Long id;
    private String name;
    private String address;
    private String city;
    private int capacity;
    private int totalSpaces;
    private int freeSpaces;
    private int occupiedSpaces;
    private BigDecimal hourlyRate;
    private String lotType;
    private Long ownerId;
    private Double latitude;
    private Double longitude;
    private String nodeId;
    private boolean nodeOnline;
    private Instant createdAt;

    public static ParkingLotResponse from(ParkingLot lot, int occupied, boolean nodeOnline) {
        int total = lot.getCapacity();
        int free = Math.max(0, total - occupied);
        return ParkingLotResponse.builder()
                .id(lot.getId())
                .name(lot.getName())
                .address(lot.getAddress())
                .city(lot.getCity())
                .capacity(total)
                .totalSpaces(total)
                .freeSpaces(free)
                .occupiedSpaces(occupied)
                .hourlyRate(lot.getHourlyRate())
                .lotType(lot.getLotType())
                .ownerId(lot.getOwnerId())
                .latitude(lot.getLatitude())
                .longitude(lot.getLongitude())
                .nodeId(lot.getNodeId())
                .nodeOnline(nodeOnline)
                .createdAt(lot.getCreatedAt())
                .build();
    }
}
