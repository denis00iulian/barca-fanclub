package com.barca.fanclub_api.dto;

import java.time.Instant;
import java.util.UUID;

public record EventResponse(
        UUID id,
        String title,
        String description,
        Instant eventDate,
        String location,
        int capacity,
        Instant priorityReservationStartsAt,
        Instant publicReservationStartsAt
) {
}
