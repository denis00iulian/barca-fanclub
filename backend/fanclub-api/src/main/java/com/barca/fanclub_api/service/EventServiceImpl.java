package com.barca.fanclub_api.service;

import com.barca.fanclub_api.dto.CreateEventRequest;
import com.barca.fanclub_api.dto.EventResponse;
import com.barca.fanclub_api.exception.ResourceNotFoundException;
import com.barca.fanclub_api.model.Event;
import com.barca.fanclub_api.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final Clock clock;

    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
        this.clock = Clock.systemUTC();
    }

    @Override
    public List<EventResponse> getAllUpcomingEvents() {
        Instant now = Instant.now(clock);
        return toEventResponseList(eventRepository.findUpcomingEvents(now));
    }

    @Override
    public EventResponse getEvent(UUID id) {
        return toEventResponse(eventRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Event not found")
                )
        );
    }

    @Override
    public EventResponse createEvent(CreateEventRequest req) {

        if (req.priorityReservationStartsAt().isAfter(req.publicReservationStartsAt())) {
            throw new IllegalArgumentException("priorityReservationStartsAt must be <= publicReservationStartsAt");
        }

        if (req.publicReservationStartsAt().isAfter(req.eventDate())) {
            throw new IllegalArgumentException("publicReservationStartsAt must be <= eventDate");
        }

        Event event = new Event(
                req.title(),
                req.description(),
                req.eventDate(),
                req.location(),
                req.capacity(),
                req.priorityReservationStartsAt(),
                req.publicReservationStartsAt()
        );

        return toEventResponse(eventRepository.save(event));
    }

    private EventResponse toEventResponse(Event event) {
        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getEventDate(),
                event.getLocation(),
                event.getCapacity(),
                event.getPriorityReservationStartsAt(),
                event.getPublicReservationStartsAt()
        );
    }

    private List<EventResponse> toEventResponseList(List<Event> eventList) {
        return eventList.stream()
                .map(this::toEventResponse)
                .toList();
    }
}
