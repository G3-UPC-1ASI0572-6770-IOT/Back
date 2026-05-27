package com.parkingnow.profile.application;

import com.parkingnow.iam.domain.User;
import com.parkingnow.iam.infrastructure.UserRepository;
import com.parkingnow.parkinglot.application.ParkingLotService;
import com.parkingnow.profile.application.dto.*;
import com.parkingnow.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final ParkingLotService lotService;

    public ProfileResponse getProfile() {
        User user = currentUser();
        Long lotId = lotService.findByOwnerId(user.getId()).map(l -> l.getId()).orElse(null);
        return ProfileResponse.from(user, lotId);
    }

    @Transactional
    public ProfileResponse update(UpdateProfileRequest req) {
        User user = currentUser();
        if (req.getFullName() != null) user.setFullName(req.getFullName());
        if (req.getPhone() != null) user.setPhone(req.getPhone());
        if (req.getJobTitle() != null) user.setJobTitle(req.getJobTitle());
        userRepository.save(user);
        Long lotId = lotService.findByOwnerId(user.getId()).map(l -> l.getId()).orElse(null);
        return ProfileResponse.from(user, lotId);
    }

    private User currentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found: " + email));
    }
}
