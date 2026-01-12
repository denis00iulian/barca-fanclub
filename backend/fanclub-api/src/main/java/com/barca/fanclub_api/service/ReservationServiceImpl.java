package com.barca.fanclub_api.service;

import com.barca.fanclub_api.dto.CreateReservationRequest;
import com.barca.fanclub_api.dto.EventAvailabilityResponse;
import com.barca.fanclub_api.dto.ReservationResponse;
import com.barca.fanclub_api.exception.ResourceNotFoundException;
import com.barca.fanclub_api.model.Event;
import com.barca.fanclub_api.model.Reservation;
import com.barca.fanclub_api.model.ReservationStatus;
import com.barca.fanclub_api.repository.EventRepository;
import com.barca.fanclub_api.repository.MembershipRepository;
import com.barca.fanclub_api.repository.ReservationRepository;
import com.barca.fanclub_api.model.User;
import com.barca.fanclub_api.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static java.lang.Math.max;

@Service
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository reservationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final Clock clock;

    public ReservationServiceImpl(ReservationRepository reservationRepository, EventRepository eventRepository, UserRepository userRepository, MembershipRepository membershipRepository) {
        this.reservationRepository = reservationRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.membershipRepository = membershipRepository;
        this.clock = Clock.systemUTC();
    }

    @Override
    public ReservationResponse getUserReservationForEvent(UUID userId, UUID eventId) {
        Reservation reservation = reservationRepository.findReservationByUserAndEvent(userId, eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        return new ReservationResponse(
                reservation.getId(),
                reservation.getUser().getId(),
                reservation.getEvent().getId(),
                reservation.getSeats(),
                reservation.getStatus()
        );
    }

    @Override
    public EventAvailabilityResponse computeAvailability(UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Event not found")
                );

        int occupied = reservationRepository.getTotalReservedSeatsForEventByStatus(eventId, ReservationStatus.CONFIRMED.name());

        int available = max(0, event.getCapacity() - occupied);

        return new EventAvailabilityResponse(eventId, event.getCapacity(), occupied, available, Instant.now(clock));
    }

    @Override
    @Transactional
    public ReservationResponse createReservation(UUID userId, UUID eventId, CreateReservationRequest request) {
        Event event = eventRepository.findEventByIdForUpdate(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        Optional<Reservation> optReservation = reservationRepository.findReservationByUserAndEvent(userId, eventId);

        if (optReservation.isPresent() && optReservation.get().getStatus() != ReservationStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already has a reservation");
        }

        Instant now = Instant.now(clock);

        if (now.isBefore(event.getPriorityReservationStartsAt())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Reservations not open yet");
        }

        if (now.isBefore(event.getPublicReservationStartsAt())) {
            boolean isMember = membershipRepository.userHasActiveMembershipAt(userId, now);
            if (!isMember) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Reservations are open only for members during priority window");
            }
        }

        int occupied = reservationRepository.getTotalReservedSeatsForEventByStatus(eventId, ReservationStatus.CONFIRMED.name());
        int available = event.getCapacity() - occupied;

        if (request.seats() > available) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("Not enough seats available (%d remaining)", available));
        }

        if (optReservation.isPresent() && optReservation.get().getStatus() == ReservationStatus.CANCELLED) {
            optReservation.get().setStatus(ReservationStatus.CONFIRMED);
        }

        Reservation reservation;

        if (optReservation.isPresent()) {
            reservation = optReservation.get();
            reservation.setStatus(ReservationStatus.CONFIRMED);
            reservation.setSeats(request.seats());
        } else {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            reservation = new Reservation(user, event, request.seats());
        }

        return toReservationResponse(reservationRepository.save(reservation));
    }

    @Transactional
    public void cancelReservation(UUID userId, UUID eventId) {
        Reservation reservation = reservationRepository.findReservationByUserAndEvent(userId, eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Reservation already cancelled");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
    }

    private ReservationResponse toReservationResponse(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getUser().getId(),
                reservation.getEvent().getId(),
                reservation.getSeats(),
                reservation.getStatus()
        );
    }
}
