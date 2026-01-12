package com.barca.fanclub_api.service;

import com.barca.fanclub_api.dto.CreateReservationRequest;
import com.barca.fanclub_api.dto.EventAvailabilityResponse;
import com.barca.fanclub_api.dto.ReservationResponse;

import java.util.UUID;

public interface ReservationService {

    ReservationResponse createReservation(UUID userId, UUID eventId, CreateReservationRequest request);

    void cancelReservation(UUID userId, UUID eventId);

    EventAvailabilityResponse computeAvailability(UUID eventId);

    ReservationResponse getUserReservationForEvent(UUID userId, UUID eventId);

}
