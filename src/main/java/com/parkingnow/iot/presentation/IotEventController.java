package com.parkingnow.iot.presentation;

import com.parkingnow.iot.application.IotEventService;
import com.parkingnow.iot.application.IotNodeService;
import com.parkingnow.iot.application.dto.IotEventRequest;
import com.parkingnow.iot.application.dto.IotEventResponse;
import com.parkingnow.iot.application.dto.IotNodeResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class IotEventController {

    private final IotEventService service;
    private final IotNodeService nodeService;

    @Value("${app.iot.key:parkingnow-iot-key}")
    private String iotKey;

    @PostMapping("/api/v1/iot/events")
    public ResponseEntity<IotEventResponse> receiveEvent(
            @RequestHeader(value = "X-IoT-Key", required = false) String key,
            @Valid @RequestBody IotEventRequest req) {
        validateKey(key);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.receiveEvent(req));
    }

    @GetMapping("/api/v1/iot/events")
    public List<IotEventResponse> getAll(@RequestParam(defaultValue = "50") int limit) {
        return service.findAll(limit);
    }

    @PostMapping("/api/v1/iot/heartbeat")
    public ResponseEntity<Map<String, Object>> heartbeat(
            @RequestHeader(value = "X-IoT-Key", required = false) String key,
            @RequestBody Map<String, Object> body) {
        validateKey(key);
        String nodeId = (String) body.get("nodeId");
        nodeService.heartbeat(nodeId);
        return ResponseEntity.ok(Map.of("received", true));
    }

    @GetMapping("/api/v1/iot/nodes/{nodeId}")
    public IotNodeResponse getNode(@PathVariable String nodeId) {
        return nodeService.findByNodeCode(nodeId);
    }

    private void validateKey(String key) {
        if (key != null && !iotKey.equals(key)) {
            throw new org.springframework.security.access.AccessDeniedException("Invalid IoT key");
        }
    }
}
