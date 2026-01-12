package com.barca.fanclub_api.controller;

import com.barca.fanclub_api.dto.MembershipResponse;
import com.barca.fanclub_api.service.MembershipService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "Memberships", description = "Membership status endpoints for logged-in users")
@RestController
@RequestMapping("/memberships")
public class MembershipController {

    private final MembershipService membershipService;

    public MembershipController(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @GetMapping("/me/current")
    public ResponseEntity<MembershipResponse> getUserCurrentMembership(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        return ResponseEntity.ok(
                membershipService.getUserCurrentMembership(userId)
        );
    }
}
