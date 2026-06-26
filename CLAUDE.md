# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Run the application
./mvnw spring-boot:run

# Build (skip tests)
./mvnw clean package -DskipTests

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=WorldscopeApplicationTests
```

## Architecture

WorldScope is a Spring Boot 3.5.16 / Java 21 MVC web app that proxies the restcountries REST API and renders results with Thymeleaf.

**Request flow:**

```
Browser → CountryController → CountryService → External REST Countries API
                ↓
         Thymeleaf template (index.html / detail.html)
```

**Key layers:**

- `CountryController` — two routes: `GET /` (list + optional `?search=` param) and `GET /country/{name}` (detail page).
- `CountryService` — wraps `RestTemplate` calls; adds a `Bearer` token header from config. All methods unwrap the nested `CountryResponse { data { objects [] } }` API envelope and return `Country` objects directly.
- `Country` model — raw API fields (`names`, `flag`, `capitals`) are `Map`/`List` to tolerate schema variations; `getCommonName()`, `getFlagUrl()`, and `getCapitalName()` extract the display values used in templates.
- `CountryResponse` / `CountryResponse.DataWrapper` — Jackson POJOs for the API envelope; both use `@JsonIgnoreProperties(ignoreUnknown = true)`.

## Code conventions

- Commentaires en **français**.
- Javadoc obligatoire sur toutes les classes `@Service` et `@Controller`.
- Les controllers ne contiennent pas de logique métier : ils délèguent aux services et alimentent le `Model`.
- Nommage Java standard : `camelCase` pour les méthodes et variables, `PascalCase` pour les classes.

## Workflow Git

- Une branche par user story : `feature/US-XX-description`.
- Format des commits : `type(scope): description` avec les types `feat`, `fix`, `refactor`, `test`, `docs`.
- Toujours créer un commit avant de merger sur `main`.

## Agents disponibles

- **Agent Review** (`/code-review`) : relit le code modifié et suggère des améliorations.
- **Agent Javadoc** : génère la Javadoc sur les classes modifiées.
- **Agent Tests** : génère les tests unitaires JUnit 5 pour les services.

## Configuration

The app always activates the `local` Spring profile (`spring.profiles.active=local` in `application.properties`). API credentials are loaded from `src/main/resources/application-local.properties`:

```properties
RESTCOUNTRIES_API_URL=<api url>
RESTCOUNTRIES_API_KEY=<bearer token>
```

These map to `restcountries.api.url` and `restcountries.api.key`, injected into `CountryService` via `@Value`.