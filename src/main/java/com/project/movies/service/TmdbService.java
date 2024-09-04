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

        // ジャンル一覧を事前に取得してキャッシュ
        Map<Integer, GenreDto> genreMap = fetchGenres();

        try {
            while (page <= maxPages) {
                String uri = UriComponentsBuilder
                        .fromHttpUrl("https://api.themoviedb.org/3/movie/popular")
                        .queryParam("api_key", apiKey).queryParam("language", "ja-JP")
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

                    // MovieDtoをMovieエンティティに変換
                    Movie movie = convertToEntity(movieDto);
                    movies.add(movieDto);

                    // Movieエンティティを保存
                    saveMovie(movie);
                }

                page++;
            }
        } catch (HttpClientErrorException e) {
            System.err.println("API呼び出し中にエラーが発生しました。: " + e.getMessage());
        }

        return movies;
    }

    // 韓国語 : ko-KR
    private Map<Integer, GenreDto> fetchGenres() throws JsonProcessingException {
        String uri = UriComponentsBuilder
                .fromHttpUrl("https://api.themoviedb.org/3/genre/movie/list")
                .queryParam("api_key", apiKey)
                .queryParam("language", "ja-JP").toUriString();

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
        movie.setOverview(movieDto.getOverview() != null ? movieDto.getOverview() : "データがありません"); // No overview
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
            // 既存の映画エンティティが存在する場合
            Movie existingMovie = existingMovieOpt.get();
            // 更新が必要なフィールドを設定
            existingMovie.setTitle(movie.getTitle());
            existingMovie.setOverview((movie.getOverview() != null && !movie.getOverview().isEmpty()) ? movie.getOverview() : "データがありません");
            existingMovie.setPosterPath(movie.getPosterPath());
            existingMovie.setGenres(movie.getGenres());
            // 更新されたエンティティを保存
            movieRepository.save(existingMovie);
        } else {
            // 新しい映画エンティティの場合
            movie.setOverview((movie.getOverview() != null && !movie.getOverview().isEmpty()) ? movie.getOverview() : "データがありません");
            movieRepository.save(movie);
        }
    }
}