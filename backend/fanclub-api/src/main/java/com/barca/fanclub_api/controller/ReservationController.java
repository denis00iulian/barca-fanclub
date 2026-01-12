package com.barca.fanclub_api.controller;

import com.barca.fanclub_api.dto.CreateReservationRequest;
import com.barca.fanclub_api.dto.EventAvailabilityResponse;
import com.barca.fanclub_api.dto.ReservationResponse;
import com.barca.fanclub_api.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@Tag(name = "Reservations", description = "Seat reservations for events")
@RestController
@RequestMapping("/events/{eventId}/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(
            summary = "Get event availability",
            description = "Returns capacity, occupied confirmed seats and remaining availability."
    )
    @GetMapping("/availability")
    public ResponseEntity<EventAvailabilityResponse> computeEventAvailability(@PathVariable UUID eventId) {
        return ResponseEntity.ok(
                reservationService.computeAvailability(eventId)
        );
    }

    @Operation(
            summary = "Create reservation for an event",
            description = """
    Creates (or re-activates) a reservation for the current user.
    During the priority window, only ACTIVE members can reserve.
    """
    )
    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@PathVariable UUID eventId, @Valid @RequestBody CreateReservationRequest request, Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        ReservationResponse response = reservationService.createReservation(userId, eventId, request);
        URI location = URI.create("/events/" + response.eventId() + "/reservations/me");

        return ResponseEntity
                .created(location)
                .body(response);
    }

    @Operation(summary = "Get my reservation for an event")
    @GetMapping("/me")
    public ResponseEntity<ReservationResponse> getUserReservationForEvent(@PathVariable UUID eventId, Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        return ResponseEntity.ok(
                reservationService.getUserReservationForEvent(userId, eventId)
        );
    }

    @Operation(summary = "Cancel my reservation for an event")
    @DeleteMapping("/me")
    public ResponseEntity<ReservationResponse> deleteReservation(@PathVariable UUID eventId, Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        reservationService.cancelReservation(userId, eventId);

        return ResponseEntity.noContent().build();
    }
}
