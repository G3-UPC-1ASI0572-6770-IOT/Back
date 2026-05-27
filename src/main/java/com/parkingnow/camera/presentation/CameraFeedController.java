package com.parkingnow.camera.presentation;

import com.parkingnow.camera.application.CameraFeedService;
import com.parkingnow.camera.application.dto.CameraFeedRequest;
import com.parkingnow.camera.application.dto.CameraFeedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/camera-feeds")
@RequiredArgsConstructor
public class CameraFeedController {

    private final CameraFeedService service;

    @PostMapping
    public ResponseEntity<CameraFeedResponse> register(@Valid @RequestBody CameraFeedRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.register(req));
    }

    @GetMapping("/{parkingLotId}")
    public List<CameraFeedResponse> getByLot(@PathVariable Long parkingLotId) {
        return service.findByLot(parkingLotId);
    }

    @GetMapping("/{parkingLotId}/latest")
    public CameraFeedResponse getLatest(@PathVariable Long parkingLotId) {
        return service.findLatestByLot(parkingLotId);
    }

    @PatchMapping("/{id}/status")
    public CameraFeedResponse updateStatus(@PathVariable Long id,
                                           @RequestBody java.util.Map<String, String> body) {
        return service.updateStatus(id, body.get("status"));
    }
}
