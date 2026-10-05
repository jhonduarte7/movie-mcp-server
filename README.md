# movie-mcp-server - Movie MCP Server of Spring AI

A Spring Boot 4 application leveraging **Spring AI 2.0.1** and **Java 25** to expose a movie catalog management system through the **Model Context Protocol (MCP)** and a RESTful API.

---

## 🌟 Highlights

- **Spring Boot 4.1.0 & Java 25**: Utilizes the latest LTS release and current Spring Boot 4 framework features.
- **Spring AI Model Context Protocol (MCP)**: Native integration using `spring-ai-starter-mcp-server-webmvc`. Exposes 15 tools, 1 resource, and 1 prompt template over Streamable HTTP transport at `/mcp`.
- **Entity-Based MCP Tool Beans**: Decentralized tool layer with 4 dedicated `@Component` beans (`MovieTools`, `GenreTools`, `AudienceTools`, `MovieRecommendationTools`).
- **Specialized Task-Based DTOs**: Segregated contracts by role (`MovieCatalogDto`, `MovieScheduleDto`, `MovieCreateDto`, `MovieUpdateDto`, and `MovieDto`).
- **Normalized Data Architecture**: Relational JPA entities (`Movie`, `Genre`, `Audience`) with screening showtimes collection.
- **Strict Layered Architecture**: Clear separation of concerns:
  - **Controllers**: Thin REST layer communicating exclusively through immutable DTO records.
  - **Services**: Business logic and transactional boundaries (`@Transactional`).
  - **Repositories**: Spring Data JPA repositories with derived query methods.
  - **Mappers**: Dedicated `@Component` bidirectional converters between entities and DTO records.
  - **Models**: JPA `@Entity` with audit lifecycle callbacks, and immutable `record` DTOs with Bean Validation.
- **In-Memory H2 Database**: Pre-seeded on startup with 10 classic movies with normalized categories, audience classifications, and screening showtimes.
- **Comprehensive Test Coverage**: 64 automated tests covering controllers, services, repositories, mappers, and MCP tools with 100% pass rate.

---

## 🛠️ Architecture Overview

```
src/main/java/com/venefast/springboot/mcpserver/app/
├── MovieMcpServerApplication.java
├── config/
│   └── DataInitializer.java
├── controllers/
│   └── MovieController.java
├── mappers/
│   └── MovieMapper.java
├── mcp/
│   └── tools/
│       ├── MovieTools.java                  # Movie entity operations (@McpTool)
│       ├── GenreTools.java                  # Genre entity operations (@McpTool)
│       ├── AudienceTools.java               # Audience entity operations (@McpTool)
│       └── MovieRecommendationTools.java    # AI recommendation prompt & resource (@McpPrompt, @McpResource)
├── models/
│   ├── dtos/
│   │   ├── MovieDto.java
│   │   ├── MovieCatalogDto.java
│   │   ├── MovieScheduleDto.java
│   │   ├── MovieCreateDto.java
│   │   └── MovieUpdateDto.java
│   └── entities/
│       ├── Audience.java
│       ├── Genre.java
│       └── Movie.java
├── repositories/
│   ├── AudienceRepository.java
│   ├── GenreRepository.java
│   └── MovieRepository.java
└── services/
    ├── MovieService.java
    └── impl/
        └── MovieServiceImpl.java
```

### 🧩 Modular DTOs & Tool Responsibilities

| DTO Record | Path | Associated Tool Component | Responsibility |
| :--- | :--- | :--- | :--- |
| `MovieCatalogDto` | `models/dtos/MovieCatalogDto.java` | `MovieTools` | Lightweight browsing, filtering, and summary overviews |
| `MovieScheduleDto` | `models/dtos/MovieScheduleDto.java` | `MovieTools` | Structured showtimes, schedules, and contextual availability messages |
| `MovieCreateDto` | `models/dtos/MovieCreateDto.java` | `MovieTools` | Creation command contract with Bean Validation constraints |
| `MovieUpdateDto` | `models/dtos/MovieUpdateDto.java` | `MovieTools` | Update command contract with Bean Validation constraints |
| `MovieDto` | `models/dtos/MovieDto.java` | `MovieController` & Base | Full canonical representation for REST endpoints and backwards compatibility |

---

## 🚀 Getting Started

### Prerequisites
- **Java 25** (e.g. Amazon Corretto 25)
- Maven Wrapper is bundled (`mvnw`, `mvnw.cmd`)

### Run Application
- **Windows**:
  ```cmd
  mvnw.cmd -DskipTests spring-boot:run
  ```
- **Linux / macOS**:
  ```bash
  ./mvnw -DskipTests spring-boot:run
  ```

### Run Tests
```cmd
mvnw.cmd test
```

---

## 📡 MCP Server Configuration for Clients

To connect an MCP client (such as Claude Desktop, Antigravity, or custom MCP clients) to this server over HTTP SSE/Streamable:

### Client configuration example (`mcpServers` JSON):
```json
{
  "mcpServers": {
    "movie-mcp-server": {
      "url": "http://localhost:8090/mcp"
    }
  }
}
```

### Available MCP Capabilities
 
| Type | Name / URI | Component | Description |
| :--- | :--- | :--- | :--- |
| **Tool** | `searchMoviesByTitle` | `MovieTools` | Search movies matching a title keyword. |
| **Tool** | `getMovieById` | `MovieTools` | Retrieve complete movie details by unique ID. |
| **Tool** | `getTopRatedMovies` | `MovieTools` | Retrieve movies with rating >= threshold. |
| **Tool** | `getAllMovies` | `MovieTools` | List all movies in the database. |
| **Tool** | `getCatalogOverview` | `MovieTools` | List simplified movie overviews (`MovieCatalogDto`) with title, duration, genres, audience, and rating. |
| **Tool** | `getMovieSchedules` | `MovieTools` | Retrieve screening showtimes and schedules for a movie by ID. |
| **Tool** | `getMovieScheduleDetails` | `MovieTools` | Retrieve structured movie showtimes and screening details (`MovieScheduleDto`) by movie ID. |
| **Tool** | `getMovieScheduleByTitle` | `MovieTools` | Search movie by title and retrieve screening schedules or non-existence message. |
| **Tool** | `addMovie` | `MovieTools` | Register a new movie into the catalog via `MovieCreateDto`. |
| **Tool** | `updateMovie` | `MovieTools` | Update an existing movie by its ID in the catalog via `MovieUpdateDto`. |
| **Tool** | `deleteMovie` | `MovieTools` | Delete a movie by its ID. |
| **Tool** | `getAllGenres` | `GenreTools` | Retrieve all distinct genres/categories in the catalog. |
| **Tool** | `getMoviesByGenre` | `GenreTools` | Find movies belonging to a genre (Sci-Fi, Drama, etc.). |
| **Tool** | `getAllAudiences` | `AudienceTools` | Retrieve all distinct audience age classifications in the catalog. |
| **Tool** | `getMoviesByAudience` | `AudienceTools` | Retrieve movies filtered by audience classification (PG-13, TE, etc.). |
| **Resource** | `movies://catalog` | `MovieRecommendationTools` | Plain-text snapshot of the entire movie catalog with categories and showtimes. |
| **Prompt** | `movieRecommendationPrompt` | `MovieRecommendationTools` | Prompt template for film recommendations by genre and schedules. |

---

## 🌐 REST API Endpoints

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/movies` | List all movies |
| `GET` | `/api/movies/{id}` | Get movie by ID |
| `GET` | `/api/movies/search?title={query}` | Search movies by title |
| `GET` | `/api/movies/genres` | List all available movie genres |
| `GET` | `/api/movies/genre/{genre}` | Filter movies by genre |
| `GET` | `/api/movies/audiences` | List all available audience classifications |
| `GET` | `/api/movies/audience/{audience}` | Filter movies by audience classification |
| `GET` | `/api/movies/{id}/schedules` | Get screening showtimes for a movie by ID |
| `GET` | `/api/movies/schedule?title={title}` | Get screening showtimes for a movie by title (case-insensitive) |
| `GET` | `/api/movies/top-rated?minRating={val}` | Top-rated movies |
| `POST` | `/api/movies` | Create movie |
| `PUT` | `/api/movies/{id}` | Update movie |
| `DELETE` | `/api/movies/{id}` | Delete movie |

### Example cURL Requests

#### Create Movie
```bash
curl -X POST http://localhost:8090/api/movies \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Dune: Part Two",
    "director": "Denis Villeneuve",
    "genre": "Sci-Fi",
    "releaseYear": 2024,
    "rating": 8.6,
    "synopsis": "Paul Atreides unites with Chani and the Fremen while seeking revenge against the conspirators who destroyed his family."
  }'
```

#### Search Movie
```bash
curl http://localhost:8090/api/movies/search?title=Inception
```

---

## 📊 Management & Database Consoles

- **H2 Web Console**: [http://localhost:8090/h2-console](http://localhost:8090/h2-console)
  - JDBC URL: `jdbc:h2:mem:moviedb`
  - User: `sa`
  - Password: *(leave blank)*
- **Actuator Health**: [http://localhost:8090/actuator/health](http://localhost:8090/actuator/health)
