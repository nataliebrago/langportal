#  Language Platform — Платформа изучения языков

RESTful API для управления курсами, пользователями и подписками на скидки.  
Разработано на **Spring Boot**, поддерживает валидацию, пагинацию и интеграцию с PostgreSQL.

---

##  Технологии

-  **Java 21**
-  **Spring Boot 3.2+**
-  **Spring Web, Data JPA, Validation**
-  **PostgreSQL** (через Docker)
-  **Flyway** — миграции БД
-  **Docker & Docker Compose**
-  **Swagger UI** — документация API
-  **JUnit 5** — тестирование

---

##  Зависимости

Убедитесь, что установлено:
- JDK 21
- Maven 3.6+
- Docker (опционально)

---

##  Запуск проекта

### Вариант 1: Локально (через Maven)
bash mvn clean package mvn spring-boot:run

Приложение запустится на:  
? [http://localhost:8080](http://localhost:8080)

---

### Вариант 2: Через Docker (с БД)
bash mvn clean package docker-compose up --build

Автоматически:
- Соберёт образ приложения
- Запустит PostgreSQL
- Применит миграции Flyway
- Запустит сервер

---

## ? API Документация

После запуска откройте:

? [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

Там вы найдёте:
- Все доступные эндпоинты
- Модели запросов и ответов
- Примеры использования

---

## ? Основные эндпоинты

### ? Пользователи
- `POST /api/users` — создать пользователя
- `GET /api/users/{id}` — получить пользователя

### ? Курсы
- `POST /api/courses` — создать курс
- `GET /api/courses` — список курсов с пагинацией
- `GET /api/courses/average-price` — средняя цена курсов
- `GET /api/courses/top-expensive` — топ-3 дорогих курса
- `PATCH /api/courses/{id}/price` — обновить цену

### ? Подписчики на скидки
- `POST /api/discount-subscribers` — подписаться на скидки
    - Проверяет:
        - Корректность email
        - Существует ли пользователь с таким email
        - Не подписан ли уже
- `GET /api/discount-subscribers/exists?email=...` — проверить, подписан ли
- `GET /api/discount-subscribers/by-email?email=...` — получить данные по email
- `GET /api/discount-subscribers/count` — общее количество подписчиков

---

## ? Пример: Подписка на скидки
http POST /api/discount-subscribers Content-Type: application/json
{ "email": "user@example.com" }
**Ответы:**
- `201 Created` — успешно подписан
- `400 Bad Request` — невалидный email или пользователь не найден
- `409 Conflict` — уже подписан

---
## ? Docker

### Образы
- Приложение: `language-platform-app:latest`
- База данных: `postgres:16`

### Сеть
- Имя: `language-platform_default`
- Порты:
    - `8080:8080` — приложение
    - `5432:5432` — PostgreSQL

---

### Структура проекта
language-platform/
??? src/
?   ??? main/
?   ?   ??? java/
?   ?   ?   ??? by/
?   ?   ?       ??? language/
?   ?   ?           ??? platform/
?   ?   ?               ??? controller/
?   ?   ?               ?   ??? CourseController.java
?   ?   ?               ?   ??? UserController.java
?   ?   ?               ?   ??? DiscountSubscriberController.java
?   ?   ?               ??? service/
?   ?   ?               ?   ??? CourseService.java
?   ?   ?               ?   ??? UserService.java
?   ?   ?               ?   ??? DiscountSubscriberService.java
?   ?   ?               ??? repository/
?   ?   ?               ?   ??? CourseRepository.java
?   ?   ?               ?   ??? UserRepository.java
?   ?   ?               ?   ??? DiscountSubscriberRepository.java
?   ?   ?               ??? dto/
?   ?   ?               ?   ??? CourseDto.java
?   ?   ?               ?   ??? UserDto.java
?   ?   ?               ?   ??? DiscountSubscriberDto.java
?   ?   ?               ??? mapper/
?   ?   ?               ?   ??? CourseMapper.java
?   ?   ?               ?   ??? DiscountSubscriberMapper.java
?   ?   ?               ??? LanguagePlatformApplication.java
?   ?   ??? resources/
?   ?       ??? application.yml
?   ?       ??? db/
?   ?       ?   ??? migration/
?   ?       ?       ??? V1__create_tables.sql
?   ?       ??? data.sql
?   ??? test/
?       ??? by/
?           ??? language/
?               ??? platform/
?                   ??? controller/
?                   ??? service/
?                   ??? repository/
??? pom.xml
??? Dockerfile
??? docker-compose.yml
??? README.md
---



## ? Лицензия

MIT License — свободное использование и модификация.

---

## ? Автор

Наталья Браго  
Email: qabrago@gmail.com  
GitHub: https://github.com/nataliebrago

---