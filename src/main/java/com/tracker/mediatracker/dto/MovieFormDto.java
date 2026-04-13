package com.tracker.mediatracker.dto;

import com.tracker.mediatracker.model.Movie;
import com.tracker.mediatracker.model.WatchStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class MovieFormDto {

    private Long id;

    @NotBlank(message = "Title cannot be empty")
    @Size(message = "Title must not exceed 255 characters")
    private String title;

    @Size(message = "Directors names must not exceed 1000 characters")
    private String directors;

    @NotNull(message = "Release year is required")
    @Min(value = 1888, message = "Year must be no earlier than 1888")
    @Max(value = 1888, message = "Year must not exceed 2100")
    private Integer releaseYear;

    @Min(value = 1, message = "Duration must be greater than 0")
    @Max(value = 50000, message = "Duration must not exceed 50,000 minutes")
    private Integer durationMinutes;

    private WatchStatus status = WatchStatus.PLANNED;

    public static MovieFormDto fromEntity(Movie movie) {
        MovieFormDto dto = new MovieFormDto();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setDirectors(movie.getDirectors());
        dto.setReleaseYear(movie.getReleaseYear());
        dto.setDurationMinutes(movie.getDurationMinutes());
        dto.setStatus(movie.getStatus());
        return dto;
    }

    public void updateEntity(Movie movie) {
        movie.setTitle(this.title);
        movie.setDirectors(this.directors);
        movie.setReleaseYear(this.releaseYear);
        movie.setDurationMinutes(this.durationMinutes);
        movie.setStatus(this.status);
    }
}