package com.barca.fanclub_api.repository;

import com.barca.fanclub_api.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {

    @Query(value = """
        SELECT *
        FROM events e
        WHERE e.event_date > :now
        ORDER BY e.event_date ASC
        """, nativeQuery = true)
    List<Event> findUpcomingEvents(Instant now);

    @Query(value = """
        SELECT *
        FROM events e
        WHERE e.id = :id
        FOR UPDATE
        """, nativeQuery = true)
    Optional<Event> findEventByIdForUpdate(UUID id);

}
