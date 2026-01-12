package com.barca.fanclub_api.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(name = "event_date", nullable = false)
    private Instant eventDate;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(nullable = false)
    private int capacity;

    @Column(name = "priority_reservation_starts_at", nullable = false)
    private Instant priorityReservationStartsAt;

    @Column(name = "public_reservation_starts_at", nullable = false)
    private Instant publicReservationStartsAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected Event() {}

    public Event(String title, String description, Instant eventDate, String location, int capacity, Instant priorityReservationStartsAt, Instant publicReservationStartsAt) {
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
        this.location = location;
        this.capacity = capacity;
        this.priorityReservationStartsAt = priorityReservationStartsAt;
        this.publicReservationStartsAt = publicReservationStartsAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getEventDate() {
        return eventDate;
    }

    public void setEventDate(Instant eventDate) {
        this.eventDate = eventDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Instant getPriorityReservationStartsAt() {
        return priorityReservationStartsAt;
    }

    public void setPriorityReservationStartsAt(Instant priorityReservationStartsAt) {
        this.priorityReservationStartsAt = priorityReservationStartsAt;
    }

    public Instant getPublicReservationStartsAt() {
        return publicReservationStartsAt;
    }

    public void setPublicReservationStartsAt(Instant publicReservationStartsAt) {
        this.publicReservationStartsAt = publicReservationStartsAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
