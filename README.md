# Spring Boot Assignment

## Requirements Coverage

| # | Requirement | Implementation |
|---|---|---|
| 1 | Spring Boot Application | Spring Boot 4.1.1 |
| 2 | Layered project structure | `controller` / `service` / `repository` / `dto` / `mapper` / `exception` / `config` |
| 3 | Postman-testable API | `postman/assignment.postman_collection.json` |
| 4 | Request/Response logging | `LoggingFilter` with correlation IDs |
| 5 | MSSQL + `@Transactional` | MSSQL 2022 Docker, `@Transactional` on writes |
| 6 | Pagination (10/page) | Enforced in `UserServiceImpl.getUsers()` |
| 7 | Nested 3rd party API | `/api/v1/external-posts/{id}` → jsonplaceholder.typicode.com |

## Quick Start

### 1. Start MSSQL (Docker)

```bash
docker run -e 'ACCEPT_EULA=Y' -e 'MSSQL_SA_PASSWORD=<YOUR_STRONG_PASSWORD>' \
  -p 1433:1433 --name sql_server_test \
  -v mssql_data:/var/opt/mssql \
  -d mcr.microsoft.com/mssql/server:2022-latest

sleep 30

docker exec -it sql_server_test /opt/mssql-tools18/bin/sqlcmd \
  -S localhost -U sa -P '<YOUR_STRONG_PASSWORD>' -C -Q "CREATE DATABASE TESTDB"

> **Note:** Replace `<YOUR_STRONG_PASSWORD>` with a password that meets
> MSSQL's policy (min 8 chars, uppercase, lowercase, digit, symbol).
> In production, use a secret manager — never commit credentials.