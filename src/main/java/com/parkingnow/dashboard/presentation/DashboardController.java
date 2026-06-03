package com.parkingnow.dashboard.presentation;

import com.parkingnow.dashboard.application.DashboardService;
import com.parkingnow.iam.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService service;
    private final UserRepository userRepository;

    @GetMapping
    public DashboardService.DashboardSummary getSummary(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long ownerId = null;
        if (userDetails != null) {
            ownerId = userRepository.findByEmail(userDetails.getUsername())
                    .map(u -> u.getId()).orElse(null);
        }
        return service.getSummary(ownerId);
    }
}
