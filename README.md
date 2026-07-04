# LocalMart AI

LocalMart AI is a smart local shopping and offer discovery platform built with Java 17, Spring Boot 3, Spring Security, JWT, Spring Data JPA, MySQL, and Thymeleaf.

## Features
- User registration and login with JWT authentication
- Local offer discovery API
- Responsive UI for home, login, and registration
- MySQL-compatible data model with H2 for local development

## Run locally
1. Ensure Java 17 and Maven are installed.
2. Clone or open the project folder.
3. Run with the local development profile:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=local
   ```
4. Open http://localhost:9050

## Project structure
- `src/main/java/com/localmart` – backend application code
- `src/main/resources/templates` – UI templates
- `src/main/resources/application.properties` – app configuration
