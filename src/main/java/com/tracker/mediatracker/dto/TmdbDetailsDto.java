package com.tracker.mediatracker.dto;

import lombok.Data;

@Data
public class TmdbDetailsDto {
    private String title;
    private Integer releaseYear;
    private Integer durationMinutes;
    private String directors;
    private Integer totalEpisodes;
}