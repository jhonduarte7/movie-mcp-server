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
│   └── DataInitializer.java                 # Seed catalog loader at application startup
├── controllers/
│   └── MovieController.java                 # REST endpoints at /api/movies returning ResponseEntity<DTO>
├── mappers/
│   └── MovieMapper.java                     # Dedicated @Component bidirectional Entity-DTO converter
├── mcp/
│   └── MovieMcpTools.java                   # Spring AI @McpTool, @McpResource, and @McpPrompt declarations
├── models/
│   ├── dtos/
│   │   └── MovieDto.java                    # Immutable Java record payload contract with Bean Validation
│   └── entities/
│       └── Movie.java                       # JPA persistence entity mapping table 'movies' with audit hooks
├── repositories/
│   └── MovieRepository.java                 # Spring Data JpaRepository interface with derived queries
└── services/
    ├── MovieService.java                    # Business contract operating strictly on DTO records
    └── impl/
        └── MovieServiceImpl.java            # Transactional service implementation orchestrating repository & mapper
```

---

## 4. MCP Server Capabilities & Tools

Exposed over Streamable HTTP transport at endpoint `POST /mcp`:

### 4.1 Tools (`@McpTool`)
- **`searchMoviesByTitle`**: Searches movies in the catalog by keyword query.
- **`getMovieById`**: Retrieves complete movie details given its unique numeric identifier.
- **`getMoviesByGenre`**: Filters movies matching a specific genre (e.g. Sci-Fi, Drama, Action, Crime, Animation).
- **`getTopRatedMovies`**: Retrieves movies meeting or exceeding a minimum rating score (default: 8.0).
- **`getAllMovies`**: Lists all available movies in the catalog.
- **`addMovie`**: Inserts a new movie record into the database.
- **`deleteMovie`**: Deletes a movie record by its identifier.

### 4.2 Resources (`@McpResource`)
- **`movies://catalog`**: Returns an up-to-date formatted plain-text catalog summary of all movies.

### 4.3 Prompts (`@McpPrompt`)
- **`movieRecommendationPrompt`**: Generates a tailored movie recommendation prompt for LLMs based on genre preference and the active database catalog.

---

## 5. REST API Endpoints

- `GET /api/movies` - List all movies
- `GET /api/movies/{id}` - Get movie by ID
- `GET /api/movies/search?title={query}` - Search movies by title
- `GET /api/movies/genre/{genre}` - Filter movies by genre
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
