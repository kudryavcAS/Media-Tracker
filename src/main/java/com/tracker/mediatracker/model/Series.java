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

    @Min(value = 1, message = "Количество эпизодов должно быть минимум 1")
    private Integer totalEpisodes;

    @Min(value = 0, message = "Количество просмотренных не может быть отрицательным")
    private Integer watchedEpisodes = 0;

    @Enumerated(EnumType.STRING)
    private SeriesType seriesType;
}