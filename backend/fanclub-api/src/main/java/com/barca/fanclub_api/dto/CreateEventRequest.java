package com.barca.fanclub_api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record CreateEventRequest(
        @NotBlank String title,
        @NotBlank String description,
        @NotNull Instant eventDate,
        @NotBlank String location,
        @NotNull @Min(1) int capacity,
        @NotNull Instant priorityReservationStartsAt,
        @NotNull Instant publicReservationStartsAt
) {
}
