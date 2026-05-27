package com.parkingnow.event.application.dto;

import com.parkingnow.event.domain.EventAlert;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data @Builder
public class EventAlertResponse {
    private Long id;
    private String severity;
    private String title;
    private String message;
    private Long lotId;
    private Long spaceId;
    private Long nodeId;
    private Instant createdAt;

    public static EventAlertResponse from(EventAlert e) {
        return EventAlertResponse.builder()
                .id(e.getId())
                .severity(e.getSeverity().name().toLowerCase())
                .title(e.getTitle())
                .message(e.getMessage())
                .lotId(e.getLotId())
                .spaceId(e.getSpaceId())
                .nodeId(e.getNodeId())
                .createdAt(e.getCreatedAt())
                .build();
    }
}
