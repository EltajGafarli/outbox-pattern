# Debezium PostgreSQL CDC Setup (Orders)

This document aligns Docker Compose, PostgreSQL permissions, and Kafka Connect registration for:

- Connector name: `pg-cdc-orders`
- Source table: `public.orders`
- Kafka Connect URL: `http://localhost:8083`

## 1) Important consistency checks

Your current snippets use mixed credentials/databases:

- Compose Postgres init: `admin` / `temp_db`
- Connector config: `order_user` / `order_db`
- SQL grants: mixed `debezium` and `order_user`

Use one consistent setup. This guide uses:

- Database: `order_db`
- Debezium connector user: `debezium`

## 2) PostgreSQL SQL setup

Run these statements on PostgreSQL (as a superuser/admin).

```sql
-- Create database once (skip if already exists)
CREATE DATABASE order_db;

-- Create CDC user for Debezium
CREATE ROLE debezium WITH LOGIN PASSWORD '<CHANGE_ME_DBZ_PASSWORD>' REPLICATION;

-- Allow login to target database
GRANT CONNECT ON DATABASE order_db TO debezium;

-- Run the rest while connected to order_db:
-- \c order_db

-- Schema usage + table read access
GRANT USAGE ON SCHEMA public TO debezium;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO debezium;
ALTER DEFAULT PRIVILEGES IN SCHEMA public
  GRANT SELECT ON TABLES TO debezium;

-- Optional but commonly required for Debezium metadata reads
GRANT pg_read_all_settings TO debezium;

-- Create publication for selected table
CREATE PUBLICATION dbz_publication FOR TABLE public.orders;
```

Notes:

- Do not duplicate grants repeatedly.
- If publication already exists, use:
  `ALTER PUBLICATION dbz_publication SET TABLE public.orders;`

## 3) Register connector (Postman)

Method: `POST`  
URL: `http://localhost:8083/connectors`  
Header: `Content-Type: application/json`

Body:

```json
{
  "name": "pg-cdc-orders",
  "config": {
    "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
    "tasks.max": "1",
    "database.hostname": "postgres",
    "database.port": "5432",
    "database.user": "debezium",
    "database.password": "<CHANGE_ME_DBZ_PASSWORD>",
    "database.dbname": "order_db",
    "topic.prefix": "cdc",
    "plugin.name": "pgoutput",
    "slot.name": "dbz_slot_appdb",
    "publication.name": "dbz_publication",
    "publication.autocreate.mode": "filtered",
    "schema.include.list": "public",
    "table.include.list": "public.orders",
    "snapshot.mode": "initial",
    "heartbeat.interval.ms": "10000"
  }
}
```

Equivalent cURL:

```bash
curl -X POST 'http://localhost:8083/connectors' \
  -H 'Content-Type: application/json' \
  -d '{
    "name": "pg-cdc-orders",
    "config": {
      "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
      "tasks.max": "1",
      "database.hostname": "postgres",
      "database.port": "5432",
      "database.user": "debezium",
      "database.password": "<CHANGE_ME_DBZ_PASSWORD>",
      "database.dbname": "order_db",
      "topic.prefix": "cdc",
      "plugin.name": "pgoutput",
      "slot.name": "dbz_slot_appdb",
      "publication.name": "dbz_publication",
      "publication.autocreate.mode": "filtered",
      "schema.include.list": "public",
      "table.include.list": "public.orders",
      "snapshot.mode": "initial",
      "heartbeat.interval.ms": "10000"
    }
  }'
```

## 4) About your current response

You shared this response:

```json
{
  "name": "pg-cdc-orders",
  "config": {
    "...": "...",
    "name": "pg-cdc-orders"
  },
  "tasks": [],
  "type": "source"
}
```

`tasks: []` means the connector was created, but task assignment/start is not active yet (or failed after creation).

Check actual runtime status:

```bash
curl http://localhost:8083/connectors/pg-cdc-orders/status
```

If failed, inspect Kafka Connect logs and verify:

- DB host/database/user/password are correct.
- Replication/publication privileges are granted to the same user used in connector config.
- `public.orders` exists.

## 5) Quick Docker Compose issues to fix

In your current `docker-compose.yaml`, these are likely blockers:

1. Postgres init values do not match connector config (`temp_db/admin` vs `order_db/debezium`).
2. Debezium UI env contains `KAFKA_CONNECT_UI_KAFKA_CONNECT_URIS: "https://apple.com"` (incorrect for local setup).
3. If you keep `order_user` in connector config, grants must be for `order_user`, not `debezium`.
