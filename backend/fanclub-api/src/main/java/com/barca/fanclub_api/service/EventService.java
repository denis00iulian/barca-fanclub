package com.barca.fanclub_api.service;

import com.barca.fanclub_api.dto.CreateEventRequest;
import com.barca.fanclub_api.dto.EventResponse;

import java.util.List;
import java.util.UUID;

public interface EventService {

    List<EventResponse> getAllUpcomingEvents();

    EventResponse getEvent(UUID id);

    EventResponse createEvent(CreateEventRequest req);

}
