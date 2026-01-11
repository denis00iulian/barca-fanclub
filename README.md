# Barca Fanclub Iași — Backend API (Spring Boot + PostgreSQL)

Backend REST API for a football fan club website: events, seat reservations with membership priority window, and basic admin workflows.

## Features (MVP)
- JWT authentication (roles: USER, ADMIN)
- Events
  - Public: list upcoming events
  - Admin: create events
- Reservations
  - Create reservation (seat count)
  - Cancel reservation
  - Rebook after cancel (same user + event)
  - Public availability endpoint (capacity / occupied / available)
- Memberships
  - Annual membership model (ACTIVE / PENDING / EXPIRED)
  - Admin activates membership for a given year
  - User can query current membership for the year

## Tech Stack
- Java 17
- Spring Boot 3 (Web, Security, Data JPA, Validation)
- PostgreSQL 15
- Flyway
- JWT (jjwt)
- Swagger UI (springdoc-openapi)

## Business Rules
### Reservation windows
Each event has:
- priorityReservationStartsAt
- publicReservationStartsAt

Rules:
- Before priorityReservationStartsAt: reservations are closed (409 Conflict)
- Between priorityReservationStartsAt and publicReservationStartsAt: members only (403 Forbidden if not member)
- After publicReservationStartsAt: anyone authenticated can reserve

### Capacity
- Capacity is enforced by computing confirmed occupied seats.
- If requested seats > available → 409 Conflict

### One reservation per user per event
- A user can have a single reservation per event.
- If the reservation is CANCELLED, creating again reactivates it.

## Local Setup

### 1) Start PostgreSQL (Docker)
```powershell
cd .\docker
docker compose up -d
```

Verify container is running:
```powershell
docker ps
```

### 2) Run Spring Boot
Run FanclubApiApplication from IntelliJ.

## API Documentation (Swagger UI)
Swagger UI:
http://localhost:8080/swagger-ui/index.html

OpenAPI JSON:
http://localhost:8080/v3/api-docs

### Testing secured endpoints
1. Call POST /auth/login
2. Copy accessToken
3. Click Authorize in Swagger UI
4. Paste token
5. Now you can test secured endpoints (/users/me, /memberships/me/current, reservations, admin endpoints, etc.)

## Demo Script (PowerShell)
A reproducible demo is available via demo.ps1.

Run:
```powershell
.\demo.ps1
```

If blocked:
```powershell
Set-ExecutionPolicy -Scope CurrentUser RemoteSigned
```

# What it does
- Registers user + admin (idempotent)
- Logs in and retrieves JWT tokens
- Calls GET /users/me to discover userId automatically
- Creates an event (admin)
- Activates membership for the user (admin)
- Creates a reservation (user)
- Cancels reservation
- Rebooks after cancel
- Runs negative tests:
  - No token → 401
  - User calling admin endpoint → 403
  - Seats invalid → 400
  - Seats > available → 409

Note: Admin user must have role ADMIN in DB (set once).

# Admin role setup (one-time)
If you do not have an endpoint to promote users, set admin role directly in DB:
```sql
UPDATE users SET role='ADMIN' WHERE email='admin1@barca.ro';
```
(Use your DB UI or psql inside the docker container.)

## Architecture Notes
- Standard layered approach: Controller → Service → Repository
- Data access via Spring Data JPA + selective native queries where readability is improved
- Reservation creation uses pessimistic locking (findByIdForUpdate) to reduce race conditions on capacity checks
- Global exception handler ensures consistent HTTP codes and user-facing error messages

## Next Steps
- React frontend
- Photo moderation flow
- News publishing workflow
- Reservation waitlist
- Membership pricing logic
- CI/CD and containerized deployment
