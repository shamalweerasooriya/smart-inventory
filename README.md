# Smart Inventory Backend

Multi-tenant SaaS Inventory Management System — Spring Boot REST API.

## Tech Stack

- Java 21, Spring Boot 3.4
- Spring Security (OAuth2 Resource Server / Keycloak)
- Spring Data JPA + PostgreSQL
- Liquibase (database migrations)
- Docker Compose (PostgreSQL + Keycloak)
- Testcontainers (integration tests)

## Prerequisites

- Java 21
- Maven 3.9+
- Docker & Docker Compose

## Quick Start

### 1. Start infrastructure (PostgreSQL + Keycloak)

```bash
docker-compose up -d
```

This starts:
- **PostgreSQL** on port `5432` (databases: `inventory_db`, `keycloak_db`)
- **Keycloak** on port `8180` (realm: `inventory-saas`)

### 2. Start the backend

```bash
mvn spring-boot:run
```

The API runs on `http://localhost:8080`.

### 3. Verify

```bash
# Health check (no auth required)
curl http://localhost:8080/api/health

# Get a token from Keycloak (seed user)
TOKEN=$(curl -s -X POST "http://localhost:8180/realms/inventory-saas/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=inventory-app" \
  -d "client_secret=inventory-app-secret" \
  -d "username=admin@demo.com" \
  -d "password=password123" | python3 -c "import sys,json;print(json.load(sys.stdin)['access_token'])")

# Authenticated health check (shows tenantId)
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/health
```

## Keycloak Configuration

The realm is auto-imported on first startup from `docker/keycloak/realm-export.json`.

| Resource | Details |
|---|---|
| Realm | `inventory-saas` |
| Frontend client | `inventory-app` (confidential) |
| Backend admin client | `inventory-backend-admin` (service account) |
| Roles | `ADMIN`, `USER` |
| Seed admin | `admin@demo.com` / `password123` (tenant: `demo-tenant-001`) |
| Seed user | `user@demo.com` / `password123` (tenant: `demo-tenant-001`) |
| Admin console | `http://localhost:8180` (admin/admin) |

## Project Structure

```
src/main/java/com/smartinventory/
  config/          — Security, CORS, WebSocket configuration
  security/        — Tenant context extraction from JWT
  controller/      — REST controllers
  service/         — Business logic
  repository/      — Spring Data JPA repositories
  model/           — JPA entities and DTOs
```

## Profiles

| Profile | Usage |
|---|---|
| `default` | Local dev — connects to localhost |
| `docker` | All services in Docker — uses container hostnames |
| `test` | Tests — disables Liquibase, uses create-drop DDL |
