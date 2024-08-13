package com.project.movies.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.project.movies.dto.GenreDto;
import com.project.movies.dto.MovieDto;
import com.project.movies.model.Genre;
import com.project.movies.model.Movie;
import com.project.movies.repository.GenreRepository;
import com.project.movies.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

@Service
public class TmdbService {

    @Value("${tmdb.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    private final MovieRepository movieRepository;

    private final GenreRepository genreRepository;

    public TmdbService(RestTemplate restTemplate, ObjectMapper objectMapper, MovieRepository movieRepository, GenreRepository genreRepository) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.movieRepository = movieRepository;
        this.genreRepository = genreRepository;
    }

    public List<MovieDto> fetchMovies() throws JsonProcessingException {
        List<MovieDto> movies = new ArrayList<>();
        int page = 1;
        final int maxPages = 20;

        // 장르 목록을 미리 가져와서 캐시
        Map<Integer, GenreDto> genreMap = fetchGenres();

        try {
            while (page <= maxPages) {
                String uri = UriComponentsBuilder
                        .fromHttpUrl("https://api.themoviedb.org/3/movie/popular")
                        .queryParam("api_key", apiKey).queryParam("language", "ko-KR")
                        .queryParam("page", page).toUriString();
                String response = restTemplate.getForObject(uri, String.class);
                JsonNode root = objectMapper.readTree(response);
                JsonNode results = root.path("results");

                if (results.isEmpty()) {
                    break;
                }

                for (JsonNode node : results) {
                    MovieDto movieDto = objectMapper.treeToValue(node, MovieDto.class);
                    movieDto.setPosterPath("https://image.tmdb.org/t/p/w500" + movieDto.getPosterPath());

                    if (node.has("genre_ids")) {
                        List<Integer> genreIds = objectMapper.readValue(node.path("genre_ids").toString(), new TypeReference<List<Integer>>() {
                        });
                        List<GenreDto> genres = new ArrayList<>();
                        for (Integer genreId : genreIds) {
                            GenreDto genreDto = genreMap.get(genreId);
                            if (genreDto != null) {
                                genres.add(genreDto);
                            }
                        }
                        movieDto.setGenres(genres);
                    }

                    // MovieDto를 Movie 엔티티로 변환
                    Movie movie = convertToEntity(movieDto);
                    movies.add(movieDto);

                    // Movie 엔티티 저장
                    saveMovie(movie);
                }

                page++;
            }
        } catch (HttpClientErrorException e) {
            System.err.println("API 호출 중 오류가 발생했습니다: " + e.getMessage());
        }

        return movies;
    }

    private Map<Integer, GenreDto> fetchGenres() throws JsonProcessingException {
        String uri = UriComponentsBuilder
                .fromHttpUrl("https://api.themoviedb.org/3/genre/movie/list")
                .queryParam("api_key", apiKey)
                .queryParam("language", "ko-KR").toUriString();

        String response = restTemplate.getForObject(uri, String.class);
        JsonNode root = objectMapper.readTree(response);
        JsonNode genresNode = root.path("genres");

        Map<Integer, GenreDto> genreMap = new HashMap<>();
        for (JsonNode genreNode : genresNode) {
            GenreDto genreDto = objectMapper.treeToValue(genreNode, GenreDto.class);
            genreMap.put(genreDto.getId(), genreDto);
        }
        return genreMap;
    }

    @Transactional
    private Movie convertToEntity(MovieDto movieDto) {
        Movie movie = new Movie();
        movie.setTmdbId(movieDto.getTmdbId());
        movie.setTitle(movieDto.getTitle());
        movie.setOverview(movieDto.getOverview() != null ? movieDto.getOverview() : "No overview");
        movie.setPosterPath(movieDto.getPosterPath());
        List<Genre> genres = new ArrayList<>();
        for (GenreDto genreDto : movieDto.getGenres()) {
            Genre genre = genreRepository.findByName(genreDto.getName());
            if (genre == null) {
                genre = new Genre();
                genre.setName(genreDto.getName());
                genreRepository.save(genre);
            }
            genres.add(genre);
        }
        movie.setGenres(genres);
        return movie;
    }

    @Transactional
    private void saveMovie(Movie movie) {
        Optional<Movie> existingMovieOpt = movieRepository.findByTmdbId(movie.getTmdbId());
        if (existingMovieOpt.isPresent()) {
            // 기존 영화 엔티티가 존재하는 경우
            Movie existingMovie = existingMovieOpt.get();
            // 업데이트가 필요한 필드들을 설정
            existingMovie.setTitle(movie.getTitle());
            existingMovie.setOverview((movie.getOverview() != null && !movie.getOverview().isEmpty()) ? movie.getOverview() : "No overview");
            existingMovie.setPosterPath(movie.getPosterPath());
            existingMovie.setGenres(movie.getGenres());
            // 업데이트된 엔티티를 저장
            movieRepository.save(existingMovie);
        } else {
            // 새 영화 엔티티인 경우
            movie.setOverview((movie.getOverview() != null && !movie.getOverview().isEmpty()) ? movie.getOverview() : "No overview");
            movieRepository.save(movie);
        }
    }
}