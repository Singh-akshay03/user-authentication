# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

`user-service` is a Spring Boot 4.1.0 microservice (Java 21) in a payment system. It handles user management and authentication. Uses an in-memory H2 database for development.

## Commands

```bash
# Build
./gradlew build

# Run the application
./gradlew bootRun

# Run all tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.payment.userservice.UserServiceApplicationTests"

# Run a single test method
./gradlew test --tests "com.payment.userservice.UserServiceApplicationTests.contextLoads"

# Clean build
./gradlew clean build
```

On Windows use `gradlew.bat` instead of `./gradlew`.

## Architecture

**Package root:** `com.payment.userservice`

**Stack:**
- Spring Boot Web MVC (REST controllers)
- Spring Data JPA with Hibernate (`ddl-auto=update` — schema auto-managed from entity classes)
- H2 in-memory database (dev/test); H2 console available at `http://localhost:8080/h2-console`
- Lombok for boilerplate reduction (use `@Data`, `@Builder`, `@NoArgsConstructor`, etc.)
- Spring Boot DevTools for hot reload during development

**Database (dev):**
- URL: `jdbc:h2:mem:userdb`, user `sa`, no password
- Schema is created/updated automatically from JPA entities on startup
- SQL logging is enabled (`show-sql=true`)

## Key Conventions

- Lombok is on the annotation processor — always pair `compileOnly 'org.projectlombok:lombok'` with `annotationProcessor 'org.projectlombok:lombok'` when adding dependencies.
- Test dependencies mirror main: `spring-boot-starter-data-jpa-test` and `spring-boot-starter-webmvc-test` are available for slice tests (`@DataJpaTest`, `@WebMvcTest`).
- Group ID: `com.payment`, artifact: `user-service`.
