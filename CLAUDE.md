# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

`user-service` is a Spring Boot 4.1.0 microservice (Java 21) for a payment system. It handles user registration, login (returning a signed JWT), and an authenticated profile endpoint, backed by an in-memory H2 database.

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

# Compile only (fast check)
./gradlew compileJava
```

On Windows use `gradlew.bat` instead of `./gradlew`.

## Architecture

**Package root:** `com.payment.userservice`

Layered structure: `controller → service → repository → model`, with `dto` for request/response shapes, `filter` for the JWT servlet filter, `config` for Spring Security, and `exception` for global error handling.

### Endpoints

| Endpoint | Auth required | Success | Failure |
|---|---|---|---|
| `POST /api/auth/register` | No | `201 {success:true, message, token:null}` | `409` email exists |
| `POST /api/auth/login` | No | `200 {success:true, message, token:"eyJ..."}` | `404` user not found / `401` wrong password |
| `GET /api/profile` | Yes (Bearer JWT) | `200 {id, email}` | `403` no/invalid token |

### Request flow for authenticated endpoints

```
Request with Authorization: Bearer <token>
  └── JwtAuthFilter (OncePerRequestFilter)
        ├── extracts token, calls JwtService.extractEmail()
        ├── validates with JwtService.isTokenValid()
        ├── sets UsernamePasswordAuthenticationToken in SecurityContextHolder
        └── passes to controller
              └── Authentication.getName() → email → UserRepository.findByEmail()
```

### Security

- **Spring Security** (`spring-boot-starter-security`) is on the classpath.
- `SecurityConfig` sets **stateless** sessions, permits `/api/auth/**` and `/h2-console/**`, requires authentication for everything else.
- `BCryptPasswordEncoder` bean is defined in `SecurityConfig`.
- JWT signed with HS256 via JJWT 0.12.x. Secret and expiry are in `application.properties` as `app.jwt.secret` (Base64) and `app.jwt.expiration-ms`.
- `UserDetailsServiceImpl` implements `UserDetailsService` so Spring's `AuthenticationManager` is DB-backed.

### Exception handling

`GlobalExceptionHandler` (`@ControllerAdvice`) maps exceptions to HTTP responses:

| Exception | Status |
|---|---|
| `UserNotFoundException` | 404 |
| `InvalidPasswordException` | 401 |
| `EmailAlreadyExistsException` | 409 |
| `ExpiredJwtException` | 401 `"Token has expired"` |
| `JwtException` | 401 `"Invalid token"` |
| `Exception` (catch-all) | 500 |

### Key design decisions

- **Single shared `AuthRequest`**: used for both register and login. Split only if registration needs extra fields.
- **`AuthResponse` has a nullable `token`**: null on register, JWT string on login.
- **Login errors are distinct**: 404 if email not found, 401 if password wrong — not a generic "invalid credentials".
- **Profile endpoint reads from `SecurityContextHolder`**: `authentication.getName()` gives the email set by `JwtAuthFilter`; no token parsing in the controller.

### Database (dev/test)

- H2 in-memory, URL `jdbc:h2:mem:userdb`, user `sa`, no password
- Schema auto-managed by Hibernate (`ddl-auto=update`)
- H2 web console: `http://localhost:8080/h2-console`
- SQL logging enabled (`show-sql=true`)

## Lombok usage

Always pair annotation processor entries:
```groovy
compileOnly 'org.projectlombok:lombok'
annotationProcessor 'org.projectlombok:lombok'
```
Use `@Data` + `@NoArgsConstructor` + `@AllArgsConstructor` for DTOs, `@Builder` on entities.

## Test slices available

`spring-boot-starter-data-jpa-test` and `spring-boot-starter-webmvc-test` are in the test classpath, enabling `@DataJpaTest` and `@WebMvcTest` slice tests without loading the full context.
