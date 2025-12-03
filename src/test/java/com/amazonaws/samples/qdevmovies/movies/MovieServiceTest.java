package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Ahoy! Unit tests for the MovieService search functionality
 * These tests ensure our treasure hunting methods work properly, matey!
 */
public class MovieServiceTest {

    private MovieService movieService;

    @BeforeEach
    public void setUp() {
        movieService = new MovieService();
    }

    @Test
    @DisplayName("Should return all movies when no search criteria provided")
    public void testGetAllMovies() {
        List<Movie> movies = movieService.getAllMovies();
        assertNotNull(movies);
        assertFalse(movies.isEmpty());
        // Based on movies.json, we should have 12 movies
        assertEquals(12, movies.size());
    }

    @Test
    @DisplayName("Should find movie by exact ID")
    public void testGetMovieById() {
        Optional<Movie> movie = movieService.getMovieById(1L);
        assertTrue(movie.isPresent());
        assertEquals("The Prison Escape", movie.get().getMovieName());
        assertEquals(1L, movie.get().getId());
    }

    @Test
    @DisplayName("Should return empty when movie ID not found")
    public void testGetMovieByIdNotFound() {
        Optional<Movie> movie = movieService.getMovieById(999L);
        assertFalse(movie.isPresent());
    }

    @Test
    @DisplayName("Should return empty when movie ID is null or invalid")
    public void testGetMovieByIdInvalid() {
        assertFalse(movieService.getMovieById(null).isPresent());
        assertFalse(movieService.getMovieById(0L).isPresent());
        assertFalse(movieService.getMovieById(-1L).isPresent());
    }

    @Test
    @DisplayName("Should search movies by name (case insensitive)")
    public void testSearchMoviesByName() {
        List<Movie> results = movieService.searchMoviesByName("prison");
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());

        // Test case insensitive
        results = movieService.searchMoviesByName("PRISON");
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    @DisplayName("Should search movies by partial name match")
    public void testSearchMoviesByPartialName() {
        List<Movie> results = movieService.searchMoviesByName("the");
        assertTrue(results.size() >= 4); // Should find multiple movies with "the" in the name
        
        // Verify all results contain "the" in the name (case insensitive)
        for (Movie movie : results) {
            assertTrue(movie.getMovieName().toLowerCase().contains("the"));
        }
    }

    @Test
    @DisplayName("Should return empty list for non-existent movie name")
    public void testSearchMoviesByNameNotFound() {
        List<Movie> results = movieService.searchMoviesByName("NonExistentMovie");
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list for null or empty name")
    public void testSearchMoviesByNameInvalid() {
        assertTrue(movieService.searchMoviesByName(null).isEmpty());
        assertTrue(movieService.searchMoviesByName("").isEmpty());
        assertTrue(movieService.searchMoviesByName("   ").isEmpty());
    }

    @Test
    @DisplayName("Should search movies by genre")
    public void testSearchMoviesByGenre() {
        List<Movie> results = movieService.searchMoviesByGenre("Drama");
        assertFalse(results.isEmpty());
        
        // Verify all results contain "Drama" in the genre
        for (Movie movie : results) {
            assertTrue(movie.getGenre().toLowerCase().contains("drama"));
        }
    }

    @Test
    @DisplayName("Should search movies by partial genre match")
    public void testSearchMoviesByPartialGenre() {
        List<Movie> results = movieService.searchMoviesByGenre("Action");
        assertFalse(results.isEmpty());
        
        // Verify all results contain "Action" in the genre
        for (Movie movie : results) {
            assertTrue(movie.getGenre().toLowerCase().contains("action"));
        }
    }

    @Test
    @DisplayName("Should return empty list for non-existent genre")
    public void testSearchMoviesByGenreNotFound() {
        List<Movie> results = movieService.searchMoviesByGenre("NonExistentGenre");
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list for null or empty genre")
    public void testSearchMoviesByGenreInvalid() {
        assertTrue(movieService.searchMoviesByGenre(null).isEmpty());
        assertTrue(movieService.searchMoviesByGenre("").isEmpty());
        assertTrue(movieService.searchMoviesByGenre("   ").isEmpty());
    }

    @Test
    @DisplayName("Should search movies with multiple criteria (AND logic)")
    public void testSearchMoviesMultipleCriteria() {
        // Search for Drama movies with "the" in the name
        List<Movie> results = movieService.searchMovies("the", null, "Drama");
        assertFalse(results.isEmpty());
        
        // Verify all results match both criteria
        for (Movie movie : results) {
            assertTrue(movie.getMovieName().toLowerCase().contains("the"));
            assertTrue(movie.getGenre().toLowerCase().contains("drama"));
        }
    }

    @Test
    @DisplayName("Should search movies by ID only")
    public void testSearchMoviesById() {
        List<Movie> results = movieService.searchMovies(null, 1L, null);
        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getId());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    @DisplayName("Should return empty list when ID search finds no match")
    public void testSearchMoviesByIdNotFound() {
        List<Movie> results = movieService.searchMovies(null, 999L, null);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should return all movies when all search criteria are empty")
    public void testSearchMoviesAllEmpty() {
        List<Movie> results = movieService.searchMovies(null, null, null);
        assertEquals(movieService.getAllMovies().size(), results.size());
    }

    @Test
    @DisplayName("Should get all unique genres")
    public void testGetAllGenres() {
        List<String> genres = movieService.getAllGenres();
        assertNotNull(genres);
        assertFalse(genres.isEmpty());
        
        // Should contain expected genres from movies.json
        assertTrue(genres.contains("Drama"));
        assertTrue(genres.contains("Crime/Drama"));
        assertTrue(genres.contains("Action/Crime"));
        assertTrue(genres.contains("Adventure/Fantasy"));
        assertTrue(genres.contains("Action/Sci-Fi"));
        
        // Should be sorted alphabetically
        for (int i = 1; i < genres.size(); i++) {
            assertTrue(genres.get(i-1).compareTo(genres.get(i)) <= 0);
        }
    }

    @Test
    @DisplayName("Should validate search requests correctly")
    public void testIsValidSearchRequest() {
        // Valid requests
        assertTrue(movieService.isValidSearchRequest("test", null, null));
        assertTrue(movieService.isValidSearchRequest(null, 1L, null));
        assertTrue(movieService.isValidSearchRequest(null, null, "Drama"));
        assertTrue(movieService.isValidSearchRequest("test", 1L, "Drama"));
        
        // Invalid requests
        assertFalse(movieService.isValidSearchRequest(null, null, null));
        assertFalse(movieService.isValidSearchRequest("", null, null));
        assertFalse(movieService.isValidSearchRequest("   ", null, null));
        assertFalse(movieService.isValidSearchRequest(null, 0L, null));
        assertFalse(movieService.isValidSearchRequest(null, -1L, null));
        assertFalse(movieService.isValidSearchRequest(null, null, ""));
        assertFalse(movieService.isValidSearchRequest(null, null, "   "));
    }

    @Test
    @DisplayName("Should handle complex search scenarios")
    public void testComplexSearchScenarios() {
        // Search for movies with "space" in name and "Sci-Fi" genre
        List<Movie> results = movieService.searchMovies("space", null, "Sci-Fi");
        assertFalse(results.isEmpty());
        
        for (Movie movie : results) {
            assertTrue(movie.getMovieName().toLowerCase().contains("space"));
            assertTrue(movie.getGenre().toLowerCase().contains("sci-fi"));
        }
        
        // Search that should return no results
        List<Movie> noResults = movieService.searchMovies("NonExistent", 999L, "FakeGenre");
        assertTrue(noResults.isEmpty());
    }
}