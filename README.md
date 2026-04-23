# SpringToDo

REST API для управления задачами (TODO) на **Spring Boot 3**.

## Возможности

- CRUD для задач: создать, получить, обновить, частично обновить, удалить.
- Пагинация списка задач через `limit/offset`.
- Валидация входящих DTO (`jakarta.validation`).
- Централизованная обработка ошибок (`@RestControllerAdvice`).
- Доступ к OpenAPI/Swagger UI.
- Кеширование через Redis (`Spring Cache`).
- Метрики и health-check через Spring Actuator.
- Flyway-миграции PostgreSQL.
- Тесты: unit, integration, repository + Testcontainers.

---

## Технологии

- Java 21
- Spring Boot 3.5
- PostgreSQL
- Redis
- Flyway
- MapStruct
- Springdoc OpenAPI (Swagger UI)
- Micrometer + Actuator
- JUnit 5 + Testcontainers + JSONAssert

---

## Быстрый старт (локально)

### 1) Требования

- JDK 21+
- Docker и Docker Compose (для PostgreSQL/Redis)
- Maven Wrapper (`./mvnw` уже в репозитории)

### 2) Запуск инфраструктуры

```bash
 docker compose up -d postgres redis
```

### 3) Запуск приложения

```bash
 ./mvnw spring-boot:run
```

По умолчанию приложение поднимется на `http://localhost:8080`.

---

## Полный запуск в Docker Compose

> Важно: `Dockerfile` копирует `target/*.jar`, поэтому сначала нужно собрать jar.

```bash
 ./mvnw clean package -DskipTests
docker compose up -d --build
```

---

## Документация и мониторинг

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Actuator Health: `http://localhost:8080/actuator/health`
- Actuator Metrics: `http://localhost:8080/actuator/metrics`
- Actuator Info: `http://localhost:8080/actuator/info`

---

## API

Базовый путь: `/api/v1/todos`

### Создать задачу

`POST /api/v1/todos`

Пример запроса:

```json
{
  "title": "Купить молоко",
  "description": "2 литра"
}
```

### Получить задачу по id

`GET /api/v1/todos/{id}`

### Получить список задач

`GET /api/v1/todos?limit=20&offset=0`

### Полностью обновить задачу

`PUT /api/v1/todos/{id}`

Пример запроса:

```json
{
  "title": "Купить молоко",
  "description": "3 литра",
  "completed": true
}
```

### Частично обновить задачу

`PATCH /api/v1/todos/{id}`

Пример запроса:

```json
{
  "completed": true
}
```

### Удалить задачу

`DELETE /api/v1/todos/{id}`

---

## Формат ошибок

При ошибках API возвращает структуру:

```json
{
  "timestamp": "2026-04-14T12:00:00Z",
  "status": 400,
  "code": "VALIDATION_ERROR",
  "message": "Request validation failed",
  "details": [
    "title: Title is required"
  ],
  "path": "/api/v1/todos"
}
```

---

## Тесты

Запуск всех тестов:

```bash
 ./mvnw test
```

> Для интеграционных тестов нужны Docker-контейнеры (Testcontainers).

