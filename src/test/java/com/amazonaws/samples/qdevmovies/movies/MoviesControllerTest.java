package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.ui.ExtendedModelMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Ahoy! Unit tests for the MoviesController with search functionality
 * These tests ensure our movie treasure hunting endpoints work properly, matey!
 */
public class MoviesControllerTest {

    private MoviesController moviesController;
    private Model model;
    private MovieService mockMovieService;
    private ReviewService mockReviewService;

    @BeforeEach
    public void setUp() {
        moviesController = new MoviesController();
        model = new ExtendedModelMap();
        
        // Create mock services with enhanced functionality
        mockMovieService = new MovieService() {
            @Override
            public List<Movie> getAllMovies() {
                return Arrays.asList(
                    new Movie(1L, "Test Movie", "Test Director", 2023, "Drama", "Test description", 120, 4.5),
                    new Movie(2L, "Action Movie", "Action Director", 2022, "Action", "Action description", 110, 4.0)
                );
            }
            
            @Override
            public Optional<Movie> getMovieById(Long id) {
                if (id == 1L) {
                    return Optional.of(new Movie(1L, "Test Movie", "Test Director", 2023, "Drama", "Test description", 120, 4.5));
                } else if (id == 2L) {
                    return Optional.of(new Movie(2L, "Action Movie", "Action Director", 2022, "Action", "Action description", 110, 4.0));
                }
                return Optional.empty();
            }
            
            @Override
            public List<Movie> searchMovies(String name, Long id, String genre) {
                List<Movie> allMovies = getAllMovies();
                List<Movie> results = new ArrayList<>();
                
                for (Movie movie : allMovies) {
                    boolean matches = true;
                    
                    if (name != null && !name.trim().isEmpty()) {
                        matches = matches && movie.getMovieName().toLowerCase().contains(name.toLowerCase());
                    }
                    
                    if (id != null && id > 0) {
                        matches = matches && movie.getId() == id;
                    }
                    
                    if (genre != null && !genre.trim().isEmpty()) {
                        matches = matches && movie.getGenre().toLowerCase().contains(genre.toLowerCase());
                    }
                    
                    if (matches) {
                        results.add(movie);
                    }
                }
                
                return results;
            }
            
            @Override
            public List<Movie> searchMoviesByName(String name) {
                return searchMovies(name, null, null);
            }
            
            @Override
            public List<Movie> searchMoviesByGenre(String genre) {
                return searchMovies(null, null, genre);
            }
            
            @Override
            public List<String> getAllGenres() {
                return Arrays.asList("Action", "Drama");
            }
            
            @Override
            public boolean isValidSearchRequest(String name, Long id, String genre) {
                boolean hasName = name != null && !name.trim().isEmpty();
                boolean hasId = id != null && id > 0;
                boolean hasGenre = genre != null && !genre.trim().isEmpty();
                return hasName || hasId || hasGenre;
            }
        };
        
        mockReviewService = new ReviewService() {
            @Override
            public List<Review> getReviewsForMovie(long movieId) {
                return new ArrayList<>();
            }
        };
        
        // Inject mocks using reflection
        try {
            java.lang.reflect.Field movieServiceField = MoviesController.class.getDeclaredField("movieService");
            movieServiceField.setAccessible(true);
            movieServiceField.set(moviesController, mockMovieService);
            
            java.lang.reflect.Field reviewServiceField = MoviesController.class.getDeclaredField("reviewService");
            reviewServiceField.setAccessible(true);
            reviewServiceField.set(moviesController, mockReviewService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock services", e);
        }
    }

    @Test
    @DisplayName("Should return movies view without search parameters")
    public void testGetMoviesWithoutSearch() {
        String result = moviesController.getMovies(model, null, null, null);
        
        assertEquals("movies", result);
        assertNotNull(model.getAttribute("movies"));
        assertEquals(false, model.getAttribute("searchPerformed"));
        assertNotNull(model.getAttribute("pirateMessage"));
        assertNotNull(model.getAttribute("allGenres"));
    }

    @Test
    @DisplayName("Should perform search by movie name")
    public void testGetMoviesWithNameSearch() {
        String result = moviesController.getMovies(model, "Test", null, null);
        
        assertEquals("movies", result);
        assertEquals(true, model.getAttribute("searchPerformed"));
        assertEquals("Test", model.getAttribute("searchName"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
    }

    @Test
    @DisplayName("Should perform search by movie ID")
    public void testGetMoviesWithIdSearch() {
        String result = moviesController.getMovies(model, null, 2L, null);
        
        assertEquals("movies", result);
        assertEquals(true, model.getAttribute("searchPerformed"));
        assertEquals(2L, model.getAttribute("searchId"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("Action Movie", movies.get(0).getMovieName());
    }

    @Test
    @DisplayName("Should perform search by genre")
    public void testGetMoviesWithGenreSearch() {
        String result = moviesController.getMovies(model, null, null, "Drama");
        
        assertEquals("movies", result);
        assertEquals(true, model.getAttribute("searchPerformed"));
        assertEquals("Drama", model.getAttribute("searchGenre"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
    }

    @Test
    @DisplayName("Should handle empty search results")
    public void testGetMoviesWithNoResults() {
        String result = moviesController.getMovies(model, "NonExistent", null, null);
        
        assertEquals("movies", result);
        assertEquals(true, model.getAttribute("searchPerformed"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertTrue(movies.isEmpty());
        
        String pirateMessage = (String) model.getAttribute("pirateMessage");
        assertTrue(pirateMessage.contains("No treasure found"));
    }

    @Test
    @DisplayName("Should return successful JSON response for valid search")
    public void testSearchMoviesApiSuccess() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMovies("Test", null, null);
        
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(true, body.get("success"));
        assertEquals(1, body.get("count"));
        assertEquals("TREASURE_FOUND", body.get("pirateCode"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) body.get("movies");
        assertEquals(1, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
    }

    @Test
    @DisplayName("Should return successful JSON response for empty search results")
    public void testSearchMoviesApiNoResults() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMovies("NonExistent", null, null);
        
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(true, body.get("success"));
        assertEquals(0, body.get("count"));
        assertEquals("NO_TREASURE_FOUND", body.get("pirateCode"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) body.get("movies");
        assertTrue(movies.isEmpty());
    }

    @Test
    @DisplayName("Should return bad request for invalid search parameters")
    public void testSearchMoviesApiInvalidParams() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMovies(null, null, null);
        
        assertEquals(400, response.getStatusCodeValue());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(false, body.get("success"));
        assertEquals("INVALID_SEARCH_PARAMS", body.get("pirateCode"));
        
        String message = (String) body.get("message");
        assertTrue(message.contains("Arrr!"));
        assertTrue(message.contains("landlubber"));
    }

    @Test
    @DisplayName("Should get movie details successfully")
    public void testGetMovieDetails() {
        String result = moviesController.getMovieDetails(1L, model);
        assertEquals("movie-details", result);
        assertNotNull(model.getAttribute("movie"));
    }

    @Test
    @DisplayName("Should handle movie not found in details")
    public void testGetMovieDetailsNotFound() {
        String result = moviesController.getMovieDetails(999L, model);
        assertEquals("error", result);
        assertEquals("Movie Not Found", model.getAttribute("title"));
    }
}
