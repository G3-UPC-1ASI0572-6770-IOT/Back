package com.parkingnow.profile.presentation;

import com.parkingnow.profile.application.ProfileService;
import com.parkingnow.profile.application.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService service;

    @GetMapping
    public ProfileResponse getProfile() {
        return service.getProfile();
    }

    @PutMapping
    public ProfileResponse update(@RequestBody UpdateProfileRequest req) {
        return service.update(req);
    }
}
