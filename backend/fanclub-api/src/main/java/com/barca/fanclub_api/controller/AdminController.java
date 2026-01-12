package com.barca.fanclub_api.controller;

import com.barca.fanclub_api.dto.ActivateMembershipResponse;
import com.barca.fanclub_api.dto.CreateEventRequest;
import com.barca.fanclub_api.dto.EventResponse;
import com.barca.fanclub_api.service.EventService;
import com.barca.fanclub_api.service.MembershipService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@Tag(name = "Admin", description = "Admin-only operations (events, membership activation)")
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final EventService eventService;
    private final MembershipService membershipService;

    public AdminController(EventService eventService, MembershipService membershipService) {
        this.eventService = eventService;
        this.membershipService = membershipService;
    }

    @PostMapping("/events")
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody CreateEventRequest request) {
        EventResponse response = eventService.createEvent(request);

        URI location = URI.create("/admin/events/" + response.id());

        return ResponseEntity
                .created(location)
                .body(response);
    }

    @PatchMapping("/users/{userId}/memberships/{year}/activate")
    public ResponseEntity<ActivateMembershipResponse> activateMembership(@PathVariable UUID userId, @PathVariable int year) {
        ActivateMembershipResponse response = membershipService.activateMembership(userId, year);

        return ResponseEntity.ok(response);
    }

}
