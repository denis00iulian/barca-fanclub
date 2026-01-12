package com.barca.fanclub_api.repository;

import com.barca.fanclub_api.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    @Query(value = """
        SELECT COALESCE(SUM(r.seats), 0)
        FROM reservations r
        WHERE r.event_id = :eventId
          AND r.status = :status
        """, nativeQuery = true)
    int getTotalReservedSeatsForEventByStatus(UUID eventId, String status);

    @Query(value = """
        SELECT *
        FROM reservations r
        WHERE r.user_id = :userId
          AND r.event_id = :eventId
        LIMIT 1
        """, nativeQuery = true)
    Optional<Reservation> findReservationByUserAndEvent(UUID userId, UUID eventId);

}
