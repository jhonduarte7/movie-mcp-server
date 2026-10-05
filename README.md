# Movie MCP Server of Spring AI

A Spring Boot 4 application leveraging **Spring AI 2.0.1** and **Java 25** to expose a movie catalog management system through the **Model Context Protocol (MCP)** and a RESTful API.

---

## 🌟 Highlights

- **Spring Boot 4.1.0 & Java 25**: Utilizes the latest LTS release and current framework features.
- **Spring AI Model Context Protocol (MCP)**: Native integration using `spring-ai-starter-mcp-server-webmvc`. Exposes 7 tools, 1 resource, and 1 prompt template over Streamable HTTP transport at `/mcp`.
- **Strict Layered Architecture**: Clear separation of concerns:
  - **Controllers**: Thin REST layer communicating exclusively through immutable DTO records.
  - **Services**: Business logic and transactional boundaries (`@Transactional`).
  - **Repositories**: Spring Data JPA repositories with derived query methods.
  - **Mappers**: Dedicated `@Component` bidirectional converters between entities and DTO records.
  - **Models**: JPA `@Entity` with audit lifecycle callbacks, and immutable `record` DTOs with Bean Validation.
- **In-Memory H2 Database**: Pre-seeded on startup with 8 classic movies (Inception, The Godfather, Interstellar, Pulp Fiction, Spirited Away, Parasite, The Dark Knight, Blade Runner 2049).
- **Comprehensive Test Coverage**: 35 automated tests covering controllers, services, repositories, mappers, and MCP tools with 100% pass rate.

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
│   └── MovieMcpTools.java
├── models/
│   ├── dtos/
│   │   └── MovieDto.java
│   └── entities/
│       └── Movie.java
├── repositories/
│   └── MovieRepository.java
└── services/
    ├── MovieService.java
    └── impl/
        └── MovieServiceImpl.java
```

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
      "url": "http://localhost:8080/mcp"
    }
  }
}
```

### Available MCP Capabilities

| Type | Name / URI | Description |
| :--- | :--- | :--- |
| **Tool** | `searchMoviesByTitle` | Search movies matching a title keyword. |
| **Tool** | `getMovieById` | Retrieve movie details by ID. |
| **Tool** | `getMoviesByGenre` | Find movies belonging to a genre (Sci-Fi, Drama, etc.). |
| **Tool** | `getTopRatedMovies` | Retrieve movies with rating >= threshold. |
| **Tool** | `getAllMovies` | List all movies in the database. |
| **Tool** | `addMovie` | Register a new movie into the catalog. |
| **Tool** | `deleteMovie` | Delete a movie by its ID. |
| **Resource** | `movies://catalog` | Plain-text snapshot of the entire movie catalog. |
| **Prompt** | `movieRecommendationPrompt` | Prompt template for film recommendations by genre. |

---

## 🌐 REST API Endpoints

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/movies` | List all movies |
| `GET` | `/api/movies/{id}` | Get movie by ID |
| `GET` | `/api/movies/search?title={query}` | Search movies by title |
| `GET` | `/api/movies/genre/{genre}` | Filter movies by genre |
| `GET` | `/api/movies/top-rated?minRating={val}` | Top-rated movies |
| `POST` | `/api/movies` | Create movie |
| `PUT` | `/api/movies/{id}` | Update movie |
| `DELETE` | `/api/movies/{id}` | Delete movie |

### Example cURL Requests

#### Create Movie
```bash
curl -X POST http://localhost:8080/api/movies \
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
curl http://localhost:8080/api/movies/search?title=Inception
```

---

## 📊 Management & Database Consoles

- **H2 Web Console**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
  - JDBC URL: `jdbc:h2:mem:moviedb`
  - User: `sa`
  - Password: *(leave blank)*
- **Actuator Health**: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
