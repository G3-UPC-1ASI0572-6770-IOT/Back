package com.parkingnow.iot.application.dto;

import com.parkingnow.iot.domain.IotNode;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data @Builder
public class IotNodeResponse {
    private Long id;
    private String nodeCode;
    private String firmware;
    private String status;
    private Instant lastSeen;
    private Long spaceId;
    private Long lotId;

    public static IotNodeResponse from(IotNode node) {
        return IotNodeResponse.builder()
                .id(node.getId())
                .nodeCode(node.getNodeCode())
                .firmware(node.getFirmware())
                .status(node.getStatus().name().toLowerCase())
                .lastSeen(node.getLastSeen())
                .spaceId(node.getSpaceId())
                .lotId(node.getLotId())
                .build();
    }
}
