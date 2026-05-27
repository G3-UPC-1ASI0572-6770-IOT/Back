package com.parkingnow.iot.presentation;

import com.parkingnow.iot.application.IotNodeService;
import com.parkingnow.iot.application.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/iot-nodes")
@RequiredArgsConstructor
public class IotNodeController {

    private final IotNodeService service;

    @GetMapping
    public List<IotNodeResponse> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public IotNodeResponse getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<IotNodeResponse> register(@Valid @RequestBody IotNodeRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.register(req));
    }

    @PatchMapping("/{id}/status")
    public IotNodeResponse updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return service.updateStatus(id, body);
    }

    // ESP32 calls this endpoint — no auth required (device can't store JWT)
    @PostMapping("/heartbeat/{nodeCode}")
    public IotNodeResponse heartbeat(@PathVariable String nodeCode) {
        return service.heartbeat(nodeCode);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
