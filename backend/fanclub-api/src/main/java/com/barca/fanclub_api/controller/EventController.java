package com.barca.fanclub_api.controller;

import com.barca.fanclub_api.dto.EventResponse;
import com.barca.fanclub_api.service.EventService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Events", description = "Public event endpoints (listing, details)")
@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllUpcomingEvents() {
        return ResponseEntity.ok(
                eventService.getAllUpcomingEvents()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEvent(@PathVariable UUID id) {
        return ResponseEntity.ok(
                eventService.getEvent(id)
        );
    }

}
