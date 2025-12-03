package com.amazonaws.samples.qdevmovies.movies;

import com.amazonaws.samples.qdevmovies.utils.MovieIconUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class MoviesController {
    private static final Logger logger = LogManager.getLogger(MoviesController.class);

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/movies")
    public String getMovies(org.springframework.ui.Model model,
                           @RequestParam(value = "name", required = false) String name,
                           @RequestParam(value = "id", required = false) Long id,
                           @RequestParam(value = "genre", required = false) String genre) {
        logger.info("Ahoy! Fetching movies with search criteria - name: '{}', id: {}, genre: '{}'", name, id, genre);
        
        List<Movie> movies;
        boolean isSearchRequest = movieService.isValidSearchRequest(name, id, genre);
        
        if (isSearchRequest) {
            movies = movieService.searchMovies(name, id, genre);
            model.addAttribute("searchPerformed", true);
            model.addAttribute("searchName", name);
            model.addAttribute("searchId", id);
            model.addAttribute("searchGenre", genre);
            
            if (movies.isEmpty()) {
                model.addAttribute("pirateMessage", "Arrr! No treasure found with those search criteria, me hearty! Try different terms to find yer movie bounty.");
            } else {
                model.addAttribute("pirateMessage", "Ahoy! Found " + movies.size() + " movies in yer treasure hunt!");
            }
        } else {
            movies = movieService.getAllMovies();
            model.addAttribute("searchPerformed", false);
            model.addAttribute("pirateMessage", "Welcome to our movie treasure chest, matey! Use the search form below to find specific treasures.");
        }
        
        model.addAttribute("movies", movies);
        model.addAttribute("allGenres", movieService.getAllGenres());
        return "movies";
    }

    /**
     * Ahoy! REST API endpoint for searching movie treasure
     * Returns JSON response for API consumers and landlubbers who prefer data over views
     */
    @GetMapping("/movies/search")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> searchMovies(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "genre", required = false) String genre) {
        
        logger.info("Ahoy! API search request received - name: '{}', id: {}, genre: '{}'", name, id, genre);
        
        Map<String, Object> response = new HashMap<>();
        
        // Validate search parameters - no empty searches allowed on this ship!
        if (!movieService.isValidSearchRequest(name, id, genre)) {
            response.put("success", false);
            response.put("message", "Arrr! Ye need to provide at least one search parameter, ye scurvy landlubber!");
            response.put("pirateCode", "INVALID_SEARCH_PARAMS");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            List<Movie> movies = movieService.searchMovies(name, id, genre);
            
            response.put("success", true);
            response.put("movies", movies);
            response.put("count", movies.size());
            
            if (movies.isEmpty()) {
                response.put("message", "No treasure found with those search criteria, me hearty! The seas be vast but yer search came up empty.");
                response.put("pirateCode", "NO_TREASURE_FOUND");
            } else {
                response.put("message", "Ahoy! Found " + movies.size() + " movies in yer treasure hunt!");
                response.put("pirateCode", "TREASURE_FOUND");
            }
            
            // Add search criteria to response for reference
            Map<String, Object> searchCriteria = new HashMap<>();
            searchCriteria.put("name", name);
            searchCriteria.put("id", id);
            searchCriteria.put("genre", genre);
            response.put("searchCriteria", searchCriteria);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Arrr! Error during movie search: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Shiver me timbers! Something went wrong during the search. Try again later, ye brave soul!");
            response.put("pirateCode", "SEARCH_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/movies/{id}/details")
    public String getMovieDetails(@PathVariable("id") Long movieId, org.springframework.ui.Model model) {
        logger.info("Fetching details for movie ID: {}", movieId);
        
        Optional<Movie> movieOpt = movieService.getMovieById(movieId);
        if (!movieOpt.isPresent()) {
            logger.warn("Movie with ID {} not found", movieId);
            model.addAttribute("title", "Movie Not Found");
            model.addAttribute("message", "Movie with ID " + movieId + " was not found.");
            return "error";
        }
        
        Movie movie = movieOpt.get();
        model.addAttribute("movie", movie);
        model.addAttribute("movieIcon", MovieIconUtils.getMovieIcon(movie.getMovieName()));
        model.addAttribute("allReviews", reviewService.getReviewsForMovie(movie.getId()));
        
        return "movie-details";
    }
}