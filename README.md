# Retention & Rewards Platform

A portfolio-grade, event-driven microservice platform inspired by a modern retention domain.

## Architecture

```text
                         +----------------------+
                         |      Clients / UI     |
                         +-----------+----------+
                                     |
                              REST / OpenAPI
                                     |
       +-----------------------------+-----------------------------+
       |                             |                             |
+------+---------+          +--------+--------+           +--------+---------+
| Player Service |          | Rewards Service |           | Retention Service |
| H2 + JPA       |          | H2 + JPA        |           | H2 + Kafka        |
+------+---------+          +--------+--------+           +--------+---------+
       |                             |                             |
       +-----------------------------+-----------------------------+
                                     |
                                   Kafka
                                     |
                           +---------+----------+
                           | Communication      |
                           | Service            |
                           | H2 + Kafka         |
                           +--------------------+
```

## Bounded contexts

- **Player Service**: player profile, preferences and activity events.
- **Rewards Service**: points, loyalty tiers, reward catalogue and reward claiming.
- **Retention Service**: evaluates player activity and creates retention actions.
- **Communication Service**: turns retention actions into channel-specific delivery records.
- **Shared module**: immutable event contracts and common value objects.

## Event flow

1. A client creates or updates a player.
2. A gameplay/deposit activity is published to Kafka.
3. Retention Service evaluates deterministic retention rules.
4. Rewards Service can award points and publish loyalty events.
5. Retention Service publishes a retention action.
6. Communication Service consumes the action and creates a delivery record.

## Key engineering concepts demonstrated

- Java 21 and Spring Boot 3.
- Domain-oriented microservices.
- Kafka event-driven integration.
- Idempotent event consumers.
- Explicit JSON event contracts without Java class-name coupling in Kafka headers.
- Transactional outbox pattern in the retention service.
- H2 database per service.
- Spring Data JPA.
- OpenAPI documentation.
- Validation and exception handling.
- Actuator health endpoints.
- Docker Compose for local infrastructure.
- Kubernetes manifests for deployment.
- Unit and integration test examples.
- Structured logging via Logback.

## Local development

The recommended path is Docker Compose. No Maven installation is required on the host.
Docker Compose builds all four microservices from source inside Docker images and starts the whole stack.

```bash
docker compose up --build
```

Or use the convenience script:

```bash
./scripts/up.sh
```

For host-side verification only:

```bash
./build.sh
```

Stop the stack:

```bash
./scripts/down.sh
```

Reset containers and local H2/log data:

```bash
./scripts/reset.sh
```

The services expose OpenAPI UI at `/swagger-ui.html` and actuator endpoints at `/actuator/health`.

## Default ports

| Service | Port |
|---|---:|
| Player Service | 8081 |
| Rewards Service | 8082 |
| Retention Service | 8083 |
| Communication Service | 8084 |
| Kafka | 9092 |
| Kafka UI | 8090 |


