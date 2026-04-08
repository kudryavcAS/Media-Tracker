package com.tracker.mediatracker.dto;

import com.tracker.mediatracker.model.Series;
import com.tracker.mediatracker.model.SeriesType;
import com.tracker.mediatracker.model.WatchStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SeriesFormDto {

    private Long id;

    @NotBlank(message = "Title cannot be empty")
    private String title;

    private String directors;

    @NotNull(message = "Release year is required")
    @Min(value = 1888, message = "Year must be no earlier than 1888")
    private Integer releaseYear;

    @Min(value = 1, message = "Duration must be greater than 0")
    private Integer durationMinutes;

    private WatchStatus status = WatchStatus.PLANNED;

    @Min(value = 1, message = "Total episodes must be at least 1")
    private Integer totalEpisodes;

    @Min(value = 0, message = "Watched episodes cannot be negative")
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