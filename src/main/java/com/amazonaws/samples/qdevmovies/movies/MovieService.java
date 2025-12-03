package com.amazonaws.samples.qdevmovies.movies;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@Service
public class MovieService {
    private static final Logger logger = LogManager.getLogger(MovieService.class);
    private final List<Movie> movies;
    private final Map<Long, Movie> movieMap;

    public MovieService() {
        this.movies = loadMoviesFromJson();
        this.movieMap = new HashMap<>();
        for (Movie movie : movies) {
            movieMap.put(movie.getId(), movie);
        }
    }

    private List<Movie> loadMoviesFromJson() {
        List<Movie> movieList = new ArrayList<>();
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("movies.json");
            if (inputStream != null) {
                Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name());
                String jsonContent = scanner.useDelimiter("\\A").next();
                scanner.close();
                
                JSONArray moviesArray = new JSONArray(jsonContent);
                for (int i = 0; i < moviesArray.length(); i++) {
                    JSONObject movieObj = moviesArray.getJSONObject(i);
                    movieList.add(new Movie(
                        movieObj.getLong("id"),
                        movieObj.getString("movieName"),
                        movieObj.getString("director"),
                        movieObj.getInt("year"),
                        movieObj.getString("genre"),
                        movieObj.getString("description"),
                        movieObj.getInt("duration"),
                        movieObj.getDouble("imdbRating")
                    ));
                }
            }
        } catch (Exception e) {
            logger.error("Failed to load movies from JSON: {}", e.getMessage());
        }
        return movieList;
    }

    public List<Movie> getAllMovies() {
        return movies;
    }

    public Optional<Movie> getMovieById(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(movieMap.get(id));
    }

    /**
     * Ahoy matey! Search for treasure (movies) using various criteria.
     * This method be the main search function that filters our movie treasure chest.
     * 
     * @param name The name of the movie to search for (partial matches allowed, case-insensitive)
     * @param id The exact ID of the movie ye be seekin'
     * @param genre The genre of movies to find in our treasure trove
     * @return A list of movies that match yer search criteria, or an empty list if no treasure be found
     */
    public List<Movie> searchMovies(String name, Long id, String genre) {
        logger.info("Ahoy! Searchin' for movie treasure with criteria - name: '{}', id: {}, genre: '{}'", 
                   name, id, genre);
        
        List<Movie> results = new ArrayList<>(movies);
        
        // Filter by name if provided - case insensitive partial matching, arrr!
        if (name != null && !name.trim().isEmpty()) {
            String searchName = name.trim().toLowerCase();
            results = results.stream()
                    .filter(movie -> movie.getMovieName().toLowerCase().contains(searchName))
                    .collect(Collectors.toList());
            logger.debug("After name filter '{}', found {} movies in the treasure chest", name, results.size());
        }
        
        // Filter by ID if provided - exact match only, no room for error on the high seas!
        if (id != null && id > 0) {
            results = results.stream()
                    .filter(movie -> movie.getId() == id)
                    .collect(Collectors.toList());
            logger.debug("After ID filter '{}', found {} movies in the treasure chest", id, results.size());
        }
        
        // Filter by genre if provided - case insensitive partial matching for flexibility
        if (genre != null && !genre.trim().isEmpty()) {
            String searchGenre = genre.trim().toLowerCase();
            results = results.stream()
                    .filter(movie -> movie.getGenre().toLowerCase().contains(searchGenre))
                    .collect(Collectors.toList());
            logger.debug("After genre filter '{}', found {} movies in the treasure chest", genre, results.size());
        }
        
        logger.info("Search complete! Found {} movies matching yer criteria, me hearty!", results.size());
        return results;
    }

    /**
     * Search movies by name only - a simpler treasure hunt!
     * 
     * @param name The name to search for (partial matches, case-insensitive)
     * @return List of movies matching the name criteria
     */
    public List<Movie> searchMoviesByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            logger.warn("Arrr! Empty name provided for search - returning empty treasure chest");
            return new ArrayList<>();
        }
        
        return searchMovies(name, null, null);
    }

    /**
     * Search movies by genre only - find all movies of a particular type!
     * 
     * @param genre The genre to search for (partial matches, case-insensitive)
     * @return List of movies matching the genre criteria
     */
    public List<Movie> searchMoviesByGenre(String genre) {
        if (genre == null || genre.trim().isEmpty()) {
            logger.warn("Arrr! Empty genre provided for search - returning empty treasure chest");
            return new ArrayList<>();
        }
        
        return searchMovies(null, null, genre);
    }

    /**
     * Get all unique genres available in our movie treasure chest
     * 
     * @return List of all unique genres, sorted alphabetically
     */
    public List<String> getAllGenres() {
        logger.debug("Gathering all genres from the treasure chest");
        return movies.stream()
                .map(Movie::getGenre)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Validate search parameters to prevent scurvy input from landlubbers
     * 
     * @param name Movie name parameter
     * @param id Movie ID parameter  
     * @param genre Movie genre parameter
     * @return true if at least one valid parameter is provided
     */
    public boolean isValidSearchRequest(String name, Long id, String genre) {
        boolean hasName = name != null && !name.trim().isEmpty();
        boolean hasId = id != null && id > 0;
        boolean hasGenre = genre != null && !genre.trim().isEmpty();
        
        boolean isValid = hasName || hasId || hasGenre;
        
        if (!isValid) {
            logger.warn("Arrr! Invalid search request - no valid parameters provided by this landlubber!");
        }
        
        return isValid;
    }
}
