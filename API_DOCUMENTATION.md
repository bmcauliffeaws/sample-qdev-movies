# Movie Search API Documentation 🏴‍☠️

Ahoy, fellow developers! This document provides comprehensive technical documentation for the Movie Search API, complete with pirate-themed responses and treasure hunting capabilities.

## Overview

The Movie Search API allows ye to search through our treasure chest of movies using various criteria. All responses include pirate-themed messages and codes to make yer integration more entertaining!

## Base URL

```
http://localhost:8080
```

## Authentication

No authentication required - this treasure be free for all landlubbers and pirates alike!

## Endpoints

### 1. Search Movies (JSON API)

**Endpoint:** `GET /movies/search`

**Description:** Search for movies using name, ID, or genre criteria. Returns JSON response with pirate-themed messages.

#### Request Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `name` | string | No | Movie name to search for (partial match, case-insensitive) |
| `id` | number | No | Exact movie ID to find (must be positive integer) |
| `genre` | string | No | Genre to search for (partial match, case-insensitive) |

**Note:** At least one parameter must be provided, or ye'll get a scurvy error!

#### Response Format

```json
{
  "success": boolean,
  "movies": [Movie],
  "count": number,
  "message": "string",
  "pirateCode": "string",
  "searchCriteria": {
    "name": "string|null",
    "id": "number|null", 
    "genre": "string|null"
  }
}
```

#### Movie Object Structure

```json
{
  "id": 1,
  "movieName": "The Prison Escape",
  "director": "John Director",
  "year": 1994,
  "genre": "Drama",
  "description": "Two imprisoned men bond over a number of years...",
  "duration": 142,
  "imdbRating": 5.0,
  "icon": "🎬"
}
```

#### Pirate Response Codes

| Code | Description |
|------|-------------|
| `TREASURE_FOUND` | Movies found matching search criteria |
| `NO_TREASURE_FOUND` | No movies match the search criteria |
| `INVALID_SEARCH_PARAMS` | No valid search parameters provided |
| `SEARCH_ERROR` | Internal server error during search |

#### HTTP Status Codes

| Status | Description |
|--------|-------------|
| 200 | Success - search completed (may have 0 results) |
| 400 | Bad Request - invalid or missing parameters |
| 500 | Internal Server Error - something went wrong |

### 2. Browse Movies with Search (HTML)

**Endpoint:** `GET /movies`

**Description:** Returns HTML page with movie grid and interactive search form. Supports same search parameters as JSON API.

#### Request Parameters

Same as JSON API - all optional, but when provided, triggers search functionality.

#### Response

Returns HTML page with:
- Interactive pirate-themed search form
- Movie results grid
- Pirate messages and feedback
- Search criteria persistence

## Usage Examples

### Basic Search Examples

#### Search by Movie Name
```bash
curl "http://localhost:8080/movies/search?name=prison"
```

**Response:**
```json
{
  "success": true,
  "movies": [
    {
      "id": 1,
      "movieName": "The Prison Escape",
      "director": "John Director",
      "year": 1994,
      "genre": "Drama",
      "description": "Two imprisoned men bond over a number of years...",
      "duration": 142,
      "imdbRating": 5.0,
      "icon": "🎬"
    }
  ],
  "count": 1,
  "message": "Ahoy! Found 1 movies in yer treasure hunt!",
  "pirateCode": "TREASURE_FOUND",
  "searchCriteria": {
    "name": "prison",
    "id": null,
    "genre": null
  }
}
```

#### Search by Genre
```bash
curl "http://localhost:8080/movies/search?genre=action"
```

#### Search by ID
```bash
curl "http://localhost:8080/movies/search?id=1"
```

#### Combined Search
```bash
curl "http://localhost:8080/movies/search?name=the&genre=drama"
```

### Advanced Search Examples

#### Case-Insensitive Search
```bash
# These all return the same results
curl "http://localhost:8080/movies/search?name=PRISON"
curl "http://localhost:8080/movies/search?name=prison"
curl "http://localhost:8080/movies/search?name=Prison"
```

#### Partial Genre Matching
```bash
# Find all movies with "Sci" in genre (matches "Action/Sci-Fi")
curl "http://localhost:8080/movies/search?genre=sci"
```

#### URL Encoding for Special Characters
```bash
# Search for movies with "Crime/Drama" genre
curl "http://localhost:8080/movies/search?genre=Crime%2FDrama"
```

### Error Examples

#### No Search Parameters
```bash
curl "http://localhost:8080/movies/search"
```

**Response (400 Bad Request):**
```json
{
  "success": false,
  "message": "Arrr! Ye need to provide at least one search parameter, ye scurvy landlubber!",
  "pirateCode": "INVALID_SEARCH_PARAMS"
}
```

#### Invalid ID
```bash
curl "http://localhost:8080/movies/search?id=-1"
```

**Response (400 Bad Request):**
```json
{
  "success": false,
  "message": "Arrr! Ye need to provide at least one search parameter, ye scurvy landlubber!",
  "pirateCode": "INVALID_SEARCH_PARAMS"
}
```

#### No Results Found
```bash
curl "http://localhost:8080/movies/search?name=nonexistent"
```

**Response (200 OK):**
```json
{
  "success": true,
  "movies": [],
  "count": 0,
  "message": "No treasure found with those search criteria, me hearty! The seas be vast but yer search came up empty.",
  "pirateCode": "NO_TREASURE_FOUND",
  "searchCriteria": {
    "name": "nonexistent",
    "id": null,
    "genre": null
  }
}
```

## Integration Guidelines

### JavaScript/AJAX Integration

```javascript
// Search for movies using fetch API
async function searchMovies(searchParams) {
    const params = new URLSearchParams();
    
    if (searchParams.name) params.append('name', searchParams.name);
    if (searchParams.id) params.append('id', searchParams.id);
    if (searchParams.genre) params.append('genre', searchParams.genre);
    
    try {
        const response = await fetch(`/movies/search?${params}`);
        const data = await response.json();
        
        if (data.success) {
            console.log(`Ahoy! Found ${data.count} movies!`);
            return data.movies;
        } else {
            console.error(`Search failed: ${data.message}`);
            return [];
        }
    } catch (error) {
        console.error('Search request failed:', error);
        return [];
    }
}

// Usage example
searchMovies({ name: 'prison', genre: 'drama' })
    .then(movies => {
        movies.forEach(movie => {
            console.log(`${movie.movieName} (${movie.year})`);
        });
    });
```

### Python Integration

```python
import requests
from urllib.parse import urlencode

def search_movies(name=None, movie_id=None, genre=None):
    """Search for movies using the pirate movie API"""
    
    params = {}
    if name:
        params['name'] = name
    if movie_id:
        params['id'] = movie_id
    if genre:
        params['genre'] = genre
    
    if not params:
        raise ValueError("Arrr! Ye need at least one search parameter!")
    
    url = f"http://localhost:8080/movies/search?{urlencode(params)}"
    
    try:
        response = requests.get(url)
        response.raise_for_status()
        
        data = response.json()
        
        if data['success']:
            print(f"Ahoy! Found {data['count']} movies!")
            return data['movies']
        else:
            print(f"Search failed: {data['message']}")
            return []
            
    except requests.RequestException as e:
        print(f"Request failed: {e}")
        return []

# Usage examples
movies = search_movies(name="prison")
action_movies = search_movies(genre="action")
specific_movie = search_movies(movie_id=1)
```

### Java Integration

```java
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class MovieSearchClient {
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl = "http://localhost:8080";
    
    public MovieSearchResponse searchMovies(String name, Long id, String genre) {
        UriComponentsBuilder builder = UriComponentsBuilder
            .fromHttpUrl(baseUrl + "/movies/search");
            
        if (name != null && !name.trim().isEmpty()) {
            builder.queryParam("name", name);
        }
        if (id != null && id > 0) {
            builder.queryParam("id", id);
        }
        if (genre != null && !genre.trim().isEmpty()) {
            builder.queryParam("genre", genre);
        }
        
        String url = builder.toUriString();
        
        try {
            return restTemplate.getForObject(url, MovieSearchResponse.class);
        } catch (Exception e) {
            System.err.println("Arrr! Search request failed: " + e.getMessage());
            return null;
        }
    }
}
```

## Rate Limiting

Currently no rate limiting is implemented - search to yer heart's content, ye treasure hunters!

## Caching

Search results are not cached - each request searches the live movie data for the most up-to-date treasure information.

## Available Movie Data

The API searches through 12 movies with the following genres:
- `Drama`
- `Crime/Drama` 
- `Action/Crime`
- `Action/Sci-Fi`
- `Adventure/Fantasy`
- `Adventure/Sci-Fi`
- `Drama/History`
- `Drama/Romance`
- `Drama/Thriller`

Movie IDs range from 1 to 12.

## Best Practices

1. **Always check the `success` field** in API responses before processing results
2. **Handle empty results gracefully** - use the `count` field to check result quantity
3. **Use appropriate HTTP status code handling** - 400 for client errors, 500 for server errors
4. **URL encode search parameters** especially when they contain special characters
5. **Implement proper error handling** for network failures and API errors
6. **Consider the pirate theme** when displaying messages to users - the API responses are designed to be fun!

## Troubleshooting

### Common Issues

**Problem:** Getting 400 Bad Request
**Solution:** Ensure at least one valid search parameter is provided

**Problem:** No results returned but expecting some
**Solution:** Check for typos, try partial matches, verify case-insensitive search

**Problem:** Search seems slow
**Solution:** The search is performed in-memory and should be fast - check network connectivity

### Debug Tips

1. Check the `searchCriteria` object in responses to verify what parameters were processed
2. Use the `pirateCode` field to understand the type of response
3. Enable debug logging to see pirate-themed log messages
4. Test with simple single-parameter searches first

## Support

For issues with this API, check the application logs for pirate-themed debug messages:

```bash
tail -f logs/application.log | grep "Ahoy\|Arrr"
```

---

*May the winds of code be at yer back, and may yer API calls always find treasure! 🏴‍☠️*