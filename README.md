# Outbox Pattern Demo

This repository demonstrates a transactional outbox flow with Spring Boot, PostgreSQL, Debezium, and Kafka.

Project module:

- `Order Service/` - Spring Boot service and Docker infrastructure

## Architecture Flow

1. Client (or mock loop) sends an order request to the Order Service.
2. Order Service writes business data (`orders`) and integration event (`outbox`) in the same DB transaction.
3. Debezium Kafka Connect monitors PostgreSQL WAL (logical replication).
4. Debezium publishes change events to Kafka topics.
5. Consumers read events from Kafka.

This avoids dual-write inconsistency between database and Kafka publishing.

## Components

- `PostgreSQL` as source-of-truth database
- `Kafka` as event broker
- `Kafka Connect (Debezium)` for CDC
- `Debezium UI` for connector management
- `Redis` (used by service config)
- `Redpanda Console` for Kafka topic visibility

## Run Locally

From `Order Service/`:

1. Create `.env` from `.env.example` and set secure values.
2. Start infrastructure:
   `docker compose -f docker/docker-compose.yaml up -d`
3. Start the Spring Boot app.
4. Register Debezium connector:
   `POST http://localhost:8083/connectors`
   (payload is documented in `Order Service/docker/DEBEZIUM_CONNECTOR_SETUP.md`)

## Verify Flow

1. Insert/place an order (or enable mock producer loop).
2. Confirm connector status:
   `GET http://localhost:8083/connectors/pg-cdc-orders/status`
3. Check Kafka topics in Redpanda Console:
   `http://localhost:8080`
4. Check Debezium UI:
   `http://localhost:8089`

## Configuration Notes

- Do not commit real credentials.
- Keep secrets only in local `.env` or secret manager.
- `.env` is ignored by git; `.env.example` is safe to commit.

