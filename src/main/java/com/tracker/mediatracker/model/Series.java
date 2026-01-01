package com.tracker.mediatracker.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("SERIES")
public class Series extends MediaItem {

    private Integer totalEpisodes;

    private Integer watchedEpisodes = 0;

    @Enumerated(EnumType.STRING)
    private SeriesType seriesType;
}