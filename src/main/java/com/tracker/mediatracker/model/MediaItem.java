package com.tracker.mediatracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "content_type")
public abstract class MediaItem {

    public static final String FIELD_TITLE = "title";
    public static final String FIELD_DIRECTORS = "directors";
    public static final String FIELD_RELEASE_YEAR = "releaseYear";
    public static final String FIELD_DURATION = "durationMinutes";
    public static final String FIELD_STATUS = "status";
    public static final String FIELD_TYPE = "seriesType";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    private String title;

    @NotNull(message = "Год выпуска обязателен")
    @Min(value = 1888, message = "Год должен быть не раньше 1888")
    private Integer releaseYear;

    @Min(value = 1, message = "Длительность должна быть больше 0")
    private Integer durationMinutes;

    private String directors;

    @Enumerated(EnumType.STRING)
    private WatchStatus status = WatchStatus.PLANNED;
}