package com.tracker.mediatracker.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("SERIES")
public class Series extends MediaItem {

    @Min(value = 1, message = "Total episodes must be at least 1")
    private Integer totalEpisodes;

    @Min(value = 0, message = "Watched episodes cannot be negative")
    private Integer watchedEpisodes = 0;

    @Enumerated(EnumType.STRING)
    private SeriesType seriesType;
}