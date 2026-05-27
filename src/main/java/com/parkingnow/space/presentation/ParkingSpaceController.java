package com.parkingnow.space.presentation;

import com.parkingnow.space.application.ParkingSpaceService;
import com.parkingnow.space.application.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ParkingSpaceController {

    private final ParkingSpaceService service;

    @GetMapping("/api/v1/parking-lots/{lotId}/spaces")
    public List<ParkingSpaceResponse> getByLot(@PathVariable Long lotId) {
        return service.findByLot(lotId);
    }

    @PostMapping("/api/v1/parking-lots/{lotId}/spaces")
    public ResponseEntity<ParkingSpaceResponse> create(@PathVariable Long lotId,
                                                        @Valid @RequestBody ParkingSpaceRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(lotId, req));
    }

    @GetMapping("/api/v1/spaces/{id}")
    public ParkingSpaceResponse getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PatchMapping("/api/v1/spaces/{id}/status")
    public ParkingSpaceResponse updateStatus(@PathVariable Long id,
                                              @RequestBody Map<String, String> body) {
        return service.updateStatus(id, body.get("status"));
    }

    @DeleteMapping("/api/v1/spaces/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
