package com.barca.fanclub_api.dto;

import com.barca.fanclub_api.model.ReservationStatus;

import java.util.UUID;

public record ReservationResponse(
        UUID id,
        UUID userId,
        UUID eventId,
        int seats,
        ReservationStatus status
) {
}
