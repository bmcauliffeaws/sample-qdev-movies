# Movie Service - Spring Boot Demo Application 🏴‍☠️

Ahoy matey! Welcome to our pirate-themed movie catalog web application built with Spring Boot, demonstrating Java application development best practices with a swashbuckling twist!

## Features

- **Movie Treasure Chest**: Browse 12 classic movies with detailed information
- **Movie Details**: View comprehensive information including director, year, genre, duration, and description
- **🆕 Pirate Movie Search**: Hunt for specific movies using our treasure map search functionality!
  - Search by movie name (partial matches, case-insensitive)
  - Search by exact movie ID
  - Search by genre (partial matches)
  - Combine multiple search criteria for precise treasure hunting
- **Customer Reviews**: Each movie includes authentic customer reviews with ratings and avatars
- **Responsive Design**: Mobile-first design that works on all devices
- **Modern Pirate UI**: Dark theme with pirate-inspired colors, gradients, and smooth animations

## Technology Stack

- **Java 8**
- **Spring Boot 2.7.18**
- **Maven** for dependency management
- **Thymeleaf** for templating
- **Log4j 2** for logging
- **JUnit 5.8.2** for testing

## Quick Start

### Prerequisites

- Java 8 or higher
- Maven 3.6+

### Run the Application

```bash
git clone https://github.com/<youruser>/sample-qdev-movies.git
cd sample-qdev-movies
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Access the Application

- **Movie Treasure Chest**: http://localhost:8080/movies
- **Movie Details**: http://localhost:8080/movies/{id}/details (where {id} is 1-12)
- **Search API**: http://localhost:8080/movies/search?name=prison&genre=drama

## Building for Production

```bash
mvn clean package
java -jar target/sample-qdev-movies-0.1.0.jar
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/amazonaws/samples/qdevmovies/
│   │       ├── movies/
│   │       │   ├── MoviesApplication.java    # Main Spring Boot application
│   │       │   ├── MoviesController.java     # REST controller with search endpoints
│   │       │   ├── MovieService.java         # Enhanced service with search functionality
│   │       │   ├── Movie.java                # Movie data model
│   │       │   ├── Review.java               # Review data model
│   │       │   └── ReviewService.java        # Review service
│   │       └── utils/
│   │           ├── MovieIconUtils.java       # Movie icon utilities
│   │           └── MovieUtils.java           # Movie validation utilities
│   └── resources/
│       ├── application.yml                   # Application configuration
│       ├── movies.json                       # Movie treasure data
│       ├── mock-reviews.json                 # Mock review data
│       ├── log4j2.xml                        # Logging configuration
│       ├── templates/
│       │   ├── movies.html                   # Enhanced with search form
│       │   └── movie-details.html            # Movie details template
│       └── static/css/                       # Pirate-themed styling
└── test/                                     # Comprehensive unit tests
```

## API Endpoints

### Get All Movies (with Search)
```
GET /movies
```
Returns an HTML page displaying movies with an interactive search form. Supports optional query parameters for searching.

**Query Parameters (all optional):**
- `name` (string): Search by movie name (partial match, case-insensitive)
- `id` (number): Search by exact movie ID
- `genre` (string): Search by genre (partial match, case-insensitive)

**Examples:**
```
# Show all movies
http://localhost:8080/movies

# Search by name
http://localhost:8080/movies?name=prison

# Search by genre
http://localhost:8080/movies?genre=drama

# Search by ID
http://localhost:8080/movies?id=1

# Combined search
http://localhost:8080/movies?name=the&genre=action
```

### 🆕 Movie Search API
```
GET /movies/search
```
Returns JSON response with search results. Perfect for API consumers and treasure hunters who prefer data over views!

**Query Parameters (at least one required):**
- `name` (string): Search by movie name (partial match, case-insensitive)
- `id` (number): Search by exact movie ID (must be positive)
- `genre` (string): Search by genre (partial match, case-insensitive)

**Response Format:**
```json
{
  "success": true,
  "movies": [...],
  "count": 2,
  "message": "Ahoy! Found 2 movies in yer treasure hunt!",
  "pirateCode": "TREASURE_FOUND",
  "searchCriteria": {
    "name": "the",
    "id": null,
    "genre": "drama"
  }
}
```

**Pirate Response Codes:**
- `TREASURE_FOUND`: Movies found matching search criteria
- `NO_TREASURE_FOUND`: No movies match the search criteria
- `INVALID_SEARCH_PARAMS`: No valid search parameters provided
- `SEARCH_ERROR`: Internal error during search

**Examples:**
```bash
# Search by name
curl "http://localhost:8080/movies/search?name=prison"

# Search by genre
curl "http://localhost:8080/movies/search?genre=action"

# Search by ID
curl "http://localhost:8080/movies/search?id=1"

# Combined search
curl "http://localhost:8080/movies/search?name=the&genre=drama"

# Invalid request (returns 400)
curl "http://localhost:8080/movies/search"
```

### Get Movie Details
```
GET /movies/{id}/details
```
Returns an HTML page with detailed movie information and customer reviews.

**Parameters:**
- `id` (path parameter): Movie ID (1-12)

**Example:**
```
http://localhost:8080/movies/1/details
```

## Search Functionality Features

### 🔍 Web Interface Search
- **Interactive Form**: Pirate-themed search form with movie name, ID, and genre fields
- **Dynamic Results**: Search results display in the same movie grid layout
- **Pirate Messages**: Fun, themed feedback messages for search results
- **Form Validation**: Client-side and server-side validation with pirate language
- **Persistent Search**: Search criteria remain in form after search
- **Clear Search**: Easy way to return to full movie catalog

### 🏴‍☠️ API Search Features
- **Flexible Matching**: Case-insensitive partial matching for names and genres
- **Combined Criteria**: Use multiple search parameters with AND logic
- **Comprehensive Validation**: Robust input validation and error handling
- **Pirate-themed Responses**: All API responses include pirate language and codes
- **Detailed Metadata**: Search criteria and result counts included in responses

### Edge Cases Handled
- Empty search results with helpful messages
- Invalid parameters (null, empty, negative IDs)
- Malformed requests with appropriate error responses
- Case-insensitive searching
- Whitespace trimming and validation

## Testing

Run the comprehensive test suite:

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=MovieServiceTest

# Run with coverage
mvn test jacoco:report
```

**Test Coverage:**
- **MovieService**: Complete coverage of all search methods and edge cases
- **MoviesController**: Full coverage of both web and API endpoints
- **Integration Tests**: End-to-end testing of search functionality
- **Edge Case Testing**: Comprehensive validation of error scenarios

## Troubleshooting

### Port 8080 already in use

Run on a different port:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### Build failures

Clean and rebuild:
```bash
mvn clean compile
```

### Search not working

Check the logs for pirate-themed debug messages:
```bash
tail -f logs/application.log | grep "Ahoy\|Arrr"
```

## Contributing

Ahoy! This project welcomes contributions from fellow pirates and landlubbers alike:

- Add more movies to the treasure chest
- Enhance the pirate UI/UX with more nautical themes
- Improve search functionality (fuzzy matching, advanced filters)
- Add more pirate language and personality
- Enhance the responsive design for mobile treasure hunting

## Sample Data

The application includes 12 movies across various genres:
- Drama: "The Prison Escape", "Life Journey"
- Crime/Drama: "The Family Boss", "Urban Stories", "The Wise Guys"
- Action/Crime: "The Masked Hero"
- Action/Sci-Fi: "Dream Heist", "The Virtual World"
- Adventure/Fantasy: "The Quest for the Ring"
- Adventure/Sci-Fi: "Space Wars: The Beginning"
- Drama/History: "The Factory Owner"
- Drama/Thriller: "Underground Club"

## License

This sample code is licensed under the MIT-0 License. See the LICENSE file.

---

*Arrr! May fair winds fill yer sails as ye navigate through our movie treasure chest! 🏴‍☠️*
