package com.barca.fanclub_api.dto;

import com.barca.fanclub_api.model.MembershipStatus;

import java.time.Instant;
import java.util.UUID;

public record MembershipResponse(
        UUID userId,
        int year,
        MembershipStatus status,
        Instant startsAt,
        Instant endsAt
) {
}
