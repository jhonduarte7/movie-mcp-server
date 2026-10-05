# AGENTS.md - Workspace & Project Governance

This document describes the agent skills, architecture rules, project structure, and operational instructions for the **Movie MCP Server of Spring AI**.

---

## 1. Project Overview

- **Project Name**: movie-mcp-server
- **Group ID**: `com.venefast.springboot`
- **Artifact ID**: `movie-mcp-server`
- **Base Package**: `com.venefast.springboot.mcpserver.app`
- **Java Version**: Java 25 (Amazon Corretto 25 LTS)
- **Spring Boot Version**: 4.1.0
- **Spring AI BOM Version**: 2.0.1
- **Protocol**: Model Context Protocol (MCP) STREAMABLE over WebMVC (`/mcp`) + REST API (`/api/movies`)

---

## 2. Active Skills & Responsibilities

| Skill Name | Location | Primary Role & Governance |
| :--- | :--- | :--- |
| **`spring-boot-best-practices-workspaces`** | `.agents/skills/spring-boot-best-practices-workspaces/SKILL.md` | Governs workspace-wide rules: Code in English, Communication in Spanish, Java 25 baseline, and skill orchestration. |
| **`spring-boot-best-practices`** | `.agents/skills/spring-boot-best-practices/SKILL.md` | Defines layered architecture patterns, Single DTO policy, record-based DTOs, dedicated mappers, Maven wrapper usage, and `application.properties` standard. |
| **`skill-creator`** | `.agents/skills/skill-creator/SKILL.md` | Guidelines for creating, evaluating, benchmarking, and maintaining agent skills. |

---

## 3. Strict Layered Architecture

The application strictly enforces separation of concerns across the following layers:

```
src/main/java/com/venefast/springboot/mcpserver/app/
├── MovieMcpServerApplication.java           # Spring Boot bootstrap & entry point
├── config/
│   └── DataInitializer.java                 # Seed catalog loader at application startup with normalized caching
├── controllers/
│   └── MovieController.java                 # REST endpoints at /api/movies returning ResponseEntity<DTO>
├── mappers/
│   └── MovieMapper.java                     # Dedicated @Component bidirectional Entity-DTO converter
├── mcp/
│   └── tools/
│       ├── MovieTools.java                  # Movie entity operations: search, browsing, schedules, CRUD (@McpTool)
│       ├── GenreTools.java                  # Genre entity operations: list genres, filter by genre (@McpTool)
│       ├── AudienceTools.java               # Audience entity operations: list audiences, filter by audience (@McpTool)
│       └── MovieRecommendationTools.java    # AI recommendation prompt & catalog resource (@McpPrompt, @McpResource)
├── models/
│   ├── dtos/
│   │   ├── MovieDto.java                    # Baseline immutable Java record payload contract
│   │   ├── MovieCatalogDto.java             # Specialized browsing & search DTO
│   │   ├── MovieScheduleDto.java            # Specialized showtimes & schedule DTO
│   │   ├── MovieCreateDto.java              # Creation command contract for MovieTools
│   │   └── MovieUpdateDto.java              # Update command contract for MovieTools
│   └── entities/
│       ├── Audience.java                    # JPA persistence entity mapping table 'audiences'
│       ├── Genre.java                       # JPA persistence entity mapping table 'genres'
│       └── Movie.java                       # JPA persistence entity mapping table 'movies' with audit hooks
├── repositories/
│   ├── AudienceRepository.java              # Spring Data JpaRepository for Audience
│   ├── GenreRepository.java                 # Spring Data JpaRepository for Genre
│   └── MovieRepository.java                 # Spring Data JpaRepository interface with derived queries
└── services/
    ├── MovieService.java                    # Business contract operating strictly on DTO records
    └── impl/
        └── MovieServiceImpl.java            # Transactional service implementation orchestrating repository & mapper
```

---

## 4. MCP Server Capabilities & Tools

Exposed over Streamable HTTP transport at endpoint `POST /mcp`:

### 4.1 Tools (`@McpTool`) - Organized by Entity

#### Movie Entity Tools (`MovieTools`)
- **`searchMoviesByTitle`**: Searches movies in the catalog by keyword query.
- **`getMovieById`**: Retrieves complete movie details given its unique numeric identifier.
- **`getTopRatedMovies`**: Retrieves movies meeting or exceeding a minimum rating score.
- **`getAllMovies`**: Lists all available movies in the catalog.
- **`getCatalogOverview`**: Lists a simplified catalog overview with title, duration, genres, audience, and rating using `MovieCatalogDto`.
- **`getMovieSchedules`**: Retrieves screening showtimes and schedules for a movie by its ID.
- **`getMovieScheduleDetails`**: Retrieves structured schedule details using `MovieScheduleDto` by movie ID.
- **`getMovieScheduleByTitle`**: Retrieves screening showtimes and schedule details for a movie searching by title.
- **`addMovie`**: Inserts a new movie record into the database via `MovieCreateDto`.
- **`updateMovie`**: Updates an existing movie record in the database via `MovieUpdateDto`.
- **`deleteMovie`**: Deletes a movie record by its identifier.

#### Genre Entity Tools (`GenreTools`)
- **`getAllGenres`**: Lists all distinct movie genres/categories available in the catalog.
- **`getMoviesByGenre`**: Filters movies matching a specific genre / category.

#### Audience Entity Tools (`AudienceTools`)
- **`getAllAudiences`**: Lists all distinct audience age classifications available in the catalog.
- **`getMoviesByAudience`**: Filters movies matching a specific audience classification rating.

### 4.2 Resources (`@McpResource`)
- **`movies://catalog`**: Returns an up-to-date formatted plain-text catalog summary of all movies with categories and showtimes (`MovieRecommendationTools`).

### 4.3 Prompts (`@McpPrompt`)
- **`movieRecommendationPrompt`**: Generates a tailored movie recommendation prompt for LLMs based on genre preference and the active database catalog (`MovieRecommendationTools`).

---

## 5. REST API Endpoints

- `GET /api/movies` - List all movies
- `GET /api/movies/{id}` - Get movie by ID
- `GET /api/movies/search?title={query}` - Search movies by title
- `GET /api/movies/genres` - List all available movie genres
- `GET /api/movies/genre/{genre}` - Filter movies by genre
- `GET /api/movies/audiences` - List all available audience classifications
- `GET /api/movies/audience/{audience}` - Filter movies by audience classification
- `GET /api/movies/{id}/schedules` - Get screening showtimes for a movie by ID
- `GET /api/movies/schedule?title={title}` - Get screening showtimes for a movie by title (case-insensitive)
- `GET /api/movies/top-rated?minRating={score}` - Retrieve top-rated movies
- `POST /api/movies` - Create a new movie (`@Valid` JSON payload)
- `PUT /api/movies/{id}` - Update an existing movie
- `DELETE /api/movies/{id}` - Delete movie by ID

---

## 6. How to Run & Verify

### Run the Application Locally
```cmd
mvnw.cmd -DskipTests spring-boot:run
```

### Run All Automated Tests
```cmd
mvnw.cmd test
```

### Access Points
- **REST API Base**: `http://localhost:8090/api/movies`
- **MCP Endpoint**: `http://localhost:8090/mcp`
- **H2 Web Console**: `http://localhost:8090/h2-console` (JDBC URL: `jdbc:h2:mem:moviedb`, User: `sa`, Password: *empty*)
- **Actuator Health**: `http://localhost:8090/actuator/health`

---

## 7. Current Project Evaluation & Health Assessment

### 7.1 Architecture & Design Scorecard: 100% (Grade A+)

| Evaluation Dimension | Status | Notes & Verification |
| :--- | :---: | :--- |
| **Layer Separation & Boundaries** | ✅ **Passed** | Strict separation: Controllers and MCP Tools interact strictly with DTO records; JPA entities are confined to the persistence/service boundary via `MovieMapper`. |
| **Entity-Based Tools Organization** | ✅ **Passed** | MCP Tools mapped directly to the domain entities (`MovieTools`, `GenreTools`, `AudienceTools`) plus specialized AI capabilities (`MovieRecommendationTools`). |
| **DTO Modularization & Task Segregation** | ✅ **Passed** | Fully modularized DTO layer (`MovieCatalogDto`, `MovieScheduleDto`, `MovieCreateDto`, `MovieUpdateDto`, `MovieDto`) aligning contract responsibility directly with tool roles. |
| **Normalized Relational Model** | ✅ **Passed** | Relational mapping for `Movie`, `Genre`, `Audience`, and `movie_schedules` with bidirectional conversion and seed caching. |
| **Case-Insensitive Resolution** | ✅ **Passed** | Implemented case-insensitive title and genre search algorithms with fallback fuzzy matching and clear non-existence feedback. |
| **Validation & Safety** | ✅ **Passed** | Bean Validation constraints applied on DTO commands (`@NotBlank`, `@Size`, `@Min`, `@Max`, `@DecimalMin`, `@DecimalMax`). |
| **Configuration Standard** | ✅ **Passed** | Standardized port `8090` uniformly configured across `application.properties`, tests, client examples, and documentation. |

### 7.2 Automated Test Coverage & Verification

- **Total Test Suite**: 64 automated tests
- **Pass Rate**: 100% (64 passed, 0 failures, 0 errors, 0 skipped)
- **Suite Breakdown**:
  - `MovieMcpServerApplicationTests` (1 test): Spring Boot 4.1.0 context bootstrapping and Spring AI MCP server registration.
  - `MovieControllerTest` (12 tests): HTTP status codes, JSON serialization, query filtering, genres, audiences, and request validation.
  - `MovieServiceTest` (24 tests): Full transactional business logic, CRUD operations, genres/audiences listing, schedule resolution, and specialized DTO contracts.
  - `MovieMapperTest` (10 tests): Bidirectional entity-DTO conversions, normalized field mapping, and in-place entity updates.
  - `MovieMcpToolsTest` (17 tests): Verification of all 15 MCP tools, 1 resource, and 1 prompt across `MovieTools`, `GenreTools`, `AudienceTools`, and `MovieRecommendationTools`.


