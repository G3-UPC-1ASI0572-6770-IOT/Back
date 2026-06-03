package com.parkingnow.space.presentation;

import com.parkingnow.space.application.ParkingSpaceService;
import com.parkingnow.space.application.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ParkingSpaceController {

    private final ParkingSpaceService service;

    @Value("${app.iot.key:parkingnow-iot-key}")
    private String iotKey;

    @GetMapping("/api/v1/spaces/parking-lot/{lotId}")
    public List<ParkingSpaceResponse> getByLot(@PathVariable Long lotId) {
        return service.findByLot(lotId);
    }

    @PostMapping("/api/v1/spaces")
    public ResponseEntity<ParkingSpaceResponse> create(@Valid @RequestBody ParkingSpaceRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req));
    }

    @GetMapping("/api/v1/spaces/{id}")
    public ParkingSpaceResponse getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PatchMapping("/api/v1/spaces/{id}/status")
    public ParkingSpaceResponse updateStatus(
            @PathVariable Long id,
            @RequestHeader(value = "X-IoT-Key", required = false) String key,
            @RequestBody Map<String, String> body) {
        if (key != null && !iotKey.equals(key)) {
            throw new org.springframework.security.access.AccessDeniedException("Invalid IoT key");
        }
        return service.updateStatus(id, body);
    }

    @DeleteMapping("/api/v1/spaces/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
