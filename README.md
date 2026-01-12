# FanClub API – Spring Boot Backend

FanClub API is a backend application that manages **events, seat reservations and memberships**, secured with **JWT authentication**.  
The project is designed as a **portfolio-ready backend**, fully runnable with **a single Docker command**, requiring no local Java or PostgreSQL installation.

---

##  Features (MVP)

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

---

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

---

##  Quick Start (TL;DR)

### Requirements
- Docker
- Docker Compose

### Run the application
```bash
docker compose up --build
```

### Access
- **Swagger UI:** http://localhost:8080/swagger-ui/index.html  
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs  

---

##  Repository Structure

```
.
├── docker-compose.yml
├── .env.example
├── README.md
├── backend/
│   └── fanclub-api/
│       ├── Dockerfile
│       ├── pom.xml
│       └── src/
└── frontend/          # (planned)
```

---

##  Authentication (JWT)

The API uses **JWT Bearer authentication** for protected endpoints.

### 1️ Login

**Endpoint**
```
POST /auth/login
```

**Request body**
```json
{
  "email": "admin@fanclub.local",
  "password": "Admin123!"
}
```

**Example (curl)**
```bash
curl -X POST "http://localhost:8080/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@fanclub.local",
    "password": "Admin123!"
  }'
```

The response contains a **JWT access token**.

---

### 2️ Authorize in Swagger

1. Open Swagger UI  
   http://localhost:8080/swagger-ui/index.html
2. Click **Authorize**
3. Paste the token in the following format:
```
Bearer <your_jwt_token>
```
4. Click **Authorize**

You can now call all protected endpoints directly from Swagger.

---

##  Demo Admin User

A demo **ADMIN** user is automatically created at startup using Flyway migrations.

- **Email:** `admin@fanclub.local`
- **Password:** `Admin123!`
- **Role:** `ADMIN`

These credentials are for **demo/portfolio purposes only**.

---

##  Example: Call a Protected Endpoint

```bash
curl -X GET "http://localhost:8080/memberships/me/current" \
  -H "Authorization: Bearer <your_jwt_token>"
```

---

##  Tech Stack

- **Java 17**
- **Spring Boot**
- **Spring Security (JWT)**
- **Spring Data JPA**
- **PostgreSQL 15**
- **Flyway** (database migrations & demo data)
- **Swagger / OpenAPI (springdoc-openapi)**
- **Docker & Docker Compose**

---

##  Docker Setup (How It Works)

- `db` service  
  Runs PostgreSQL 15 and initializes the database.

- `api` service  
  Builds and runs the Spring Boot application.

The API connects to the database using Docker’s internal network (`db:5432`).

### Useful commands

Stop containers:
```bash
docker compose down
```

Stop and remove database data:
```bash
docker compose down -v
```

Rebuild after code changes:
```bash
docker compose up --build
```

---

##  Notes for Reviewers

- The database schema and demo data are initialized automatically using Flyway.
- The application runs entirely in Docker using a single command.
- No local Java or PostgreSQL installation is required.
- Swagger UI is the recommended way to explore and test the API.

---

##  Future Improvements

- Frontend application (React / Angular)
- Role-based access refinements
- CI pipeline (GitHub Actions)
- Production-ready security hardening

---

##  Author

This project was built as a **portfolio backend project** to demonstrate:
- clean API design
- authentication & authorization
- Dockerized environments
- production-oriented project structure
