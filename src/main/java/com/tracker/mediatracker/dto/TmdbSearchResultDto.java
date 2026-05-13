package com.tracker.mediatracker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmdbSearchResultDto {
    private List<TmdbItem> results;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TmdbItem {
        private Long id;
        private String title;
        private String name;
        @JsonProperty("release_date")
        private String releaseDate;
        @JsonProperty("first_air_date")
        private String firstAirDate;
        @JsonProperty("media_type")
        private String mediaType;
    }
}