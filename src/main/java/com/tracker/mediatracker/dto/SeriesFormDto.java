package com.tracker.mediatracker.dto;

import com.tracker.mediatracker.model.Series;
import com.tracker.mediatracker.model.SeriesType;
import com.tracker.mediatracker.model.WatchStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SeriesFormDto {

    private Long id;

    @NotBlank(message = "Title cannot be empty")
    @Size(message = "Title must not exceed 255 characters")
    private String title;

    @Size(message = "Directors names must not exceed 1000 characters")
    private String directors;

    @NotNull(message = "Release year is required")
    @Min(value = 1888, message = "Year must be no earlier than 1888")
    @Max(value = 2100, message = "Year must not exceed 2100")
    private Integer releaseYear;

    @Min(value = 1, message = "Duration must be greater than 0")
    @Max(value = Integer.MAX_VALUE, message = "Duration must not exceed " + Integer.MAX_VALUE + " minutes")
    private Integer durationMinutes;

    private WatchStatus status = WatchStatus.PLANNED;

    @Min(value = 1, message = "Total episodes must be at least 1")
    @Max(value = 50000, message = "Total episodes must not exceed 50,000")
    private Integer totalEpisodes;

    @Min(value = 0, message = "Watched episodes cannot be negative")
    @Max(value = 50000, message = "Watched episodes must not exceed 50,000")
    private Integer watchedEpisodes = 0;

    private SeriesType seriesType;

    public static SeriesFormDto fromEntity(Series series) {
        SeriesFormDto dto = new SeriesFormDto();
        dto.setId(series.getId());
        dto.setTitle(series.getTitle());
        dto.setDirectors(series.getDirectors());
        dto.setReleaseYear(series.getReleaseYear());
        dto.setDurationMinutes(series.getDurationMinutes());
        dto.setStatus(series.getStatus());
        dto.setTotalEpisodes(series.getTotalEpisodes());
        dto.setWatchedEpisodes(series.getWatchedEpisodes());
        dto.setSeriesType(series.getSeriesType());
        return dto;
    }

    public void updateEntity(Series series) {
        series.setTitle(this.title);
        series.setDirectors(this.directors);
        series.setReleaseYear(this.releaseYear);
        series.setDurationMinutes(this.durationMinutes);
        series.setStatus(this.status);
        series.setTotalEpisodes(this.totalEpisodes);
        series.setWatchedEpisodes(this.watchedEpisodes);
        series.setSeriesType(this.seriesType);
    }
}