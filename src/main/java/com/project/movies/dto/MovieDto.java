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

    @JsonProperty("poster_path")	// この部分が重要、 JSONのキーと一致する必要があります。
    private String posterPath; // ポスター画像のパス

    @JsonProperty("id")
    private Long tmdbId; // TMDBから提供される映画のユニークID

    private List<GenreDto> genres = new ArrayList<>();
}