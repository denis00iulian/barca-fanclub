package com.barca.fanclub_api.dto;

import java.time.Instant;
import java.util.UUID;

public record EventAvailabilityResponse(
        UUID eventId,
        int capacity,
        int occupied,
        int available,
        Instant asOf
) {
}
