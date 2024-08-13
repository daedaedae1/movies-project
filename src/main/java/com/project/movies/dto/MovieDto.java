package com.project.movies.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MovieDto {

    private String title;

    private String overview;

    @JsonProperty("poster_path")	// 이 부분이 중요. JSON의 key와 일치해야 .
    private String posterPath; // 포스터 이미지 경로

    @JsonProperty("id")
    private Long tmdbId; // TMDB에서 제공하는 영화의 고유 ID

    private List<GenreDto> genres = new ArrayList<>();
}