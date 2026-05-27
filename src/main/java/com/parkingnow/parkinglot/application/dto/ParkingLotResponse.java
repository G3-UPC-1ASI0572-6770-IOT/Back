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
    private int occupied;
    private BigDecimal hourlyRate;
    private String status;
    private String lotType;
    private Long ownerId;
    private int iotNodes;
    private double rating;
    private Instant createdAt;

    public static ParkingLotResponse from(ParkingLot lot, int occupied, int iotNodes) {
        return ParkingLotResponse.builder()
                .id(lot.getId())
                .name(lot.getName())
                .address(lot.getAddress())
                .city(lot.getCity())
                .capacity(lot.getCapacity())
                .occupied(occupied)
                .hourlyRate(lot.getHourlyRate())
                .status(lot.getStatus().name().toLowerCase())
                .lotType(lot.getLotType())
                .ownerId(lot.getOwnerId())
                .iotNodes(iotNodes)
                .rating(lot.getRating())
                .createdAt(lot.getCreatedAt())
                .build();
    }
}
