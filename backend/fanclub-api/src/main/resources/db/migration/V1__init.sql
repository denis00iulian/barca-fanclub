CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- USERS
CREATE TABLE IF NOT EXISTS users (
                                     id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email         TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    name          TEXT NOT NULL,
    role          TEXT NOT NULL DEFAULT 'USER',
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_users_role CHECK (role IN ('USER', 'ADMIN'))
    );

-- MEMBERSHIPS
CREATE TABLE IF NOT EXISTS memberships (
                                           id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    year        INT  NOT NULL,
    status      TEXT NOT NULL DEFAULT 'PENDING',
    starts_at   TIMESTAMPTZ NULL,
    ends_at     TIMESTAMPTZ NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT uq_memberships_user_year UNIQUE (user_id, year),
    CONSTRAINT chk_memberships_year CHECK (year >= 2000 AND year <= 2100),
    CONSTRAINT chk_memberships_status CHECK (status IN ('PENDING', 'ACTIVE', 'EXPIRED'))
    );

-- EVENTS
CREATE TABLE IF NOT EXISTS events (
                                      id                               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title                            TEXT NOT NULL,
    description                      TEXT NOT NULL,
    event_date                       TIMESTAMPTZ NOT NULL,
    location                         TEXT NOT NULL,
    capacity                         INT NOT NULL,

    priority_reservation_starts_at    TIMESTAMPTZ NOT NULL,
    public_reservation_starts_at      TIMESTAMPTZ NOT NULL,

    created_at                        TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT chk_events_capacity CHECK (capacity > 0),
    CONSTRAINT chk_events_reservation_windows CHECK (
                                                        priority_reservation_starts_at <= public_reservation_starts_at
                                                    )
    );

-- RESERVATIONS
CREATE TABLE IF NOT EXISTS reservations (
                                            id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    event_id    UUID NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    seats       INT NOT NULL DEFAULT 1,
    status      TEXT NOT NULL DEFAULT 'CONFIRMED',
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT uq_reservations_user_event UNIQUE (user_id, event_id),
    CONSTRAINT chk_reservations_seats CHECK (seats > 0),
    CONSTRAINT chk_reservations_status CHECK (status IN ('CONFIRMED', 'WAITLISTED', 'CANCELLED'))
    );

-- NEWS POSTS
CREATE TABLE IF NOT EXISTS news_posts (
                                          id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title       TEXT NOT NULL,
    content     TEXT NOT NULL,
    published   BOOLEAN NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
    );

-- PHOTOS
CREATE TABLE IF NOT EXISTS photos (
                                      id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    image_url    TEXT NOT NULL,
    caption      TEXT NULL,
    approved     BOOLEAN NOT NULL DEFAULT FALSE,
    uploaded_by  UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    event_id     UUID NULL REFERENCES events(id) ON DELETE SET NULL,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now()
    );

-- Indexes
CREATE INDEX IF NOT EXISTS idx_memberships_user ON memberships(user_id);
CREATE INDEX IF NOT EXISTS idx_memberships_year ON memberships(year);
CREATE INDEX IF NOT EXISTS idx_events_date ON events(event_date);
CREATE INDEX IF NOT EXISTS idx_reservations_event ON reservations(event_id);
CREATE INDEX IF NOT EXISTS idx_photos_event ON photos(event_id);
CREATE INDEX IF NOT EXISTS idx_news_published ON news_posts(published);
