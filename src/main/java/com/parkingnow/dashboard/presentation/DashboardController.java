package com.parkingnow.dashboard.presentation;

import com.parkingnow.dashboard.application.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService service;

    @GetMapping
    public DashboardService.DashboardSummary getSummary(
            @RequestParam(required = false) Long ownerId) {
        return service.getSummary(ownerId);
    }
}
