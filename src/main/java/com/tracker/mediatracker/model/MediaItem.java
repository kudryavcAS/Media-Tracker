package com.tracker.mediatracker.model;

import jakarta.persistence.*;
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

    private String title;

    private Integer releaseYear;

    private Integer durationMinutes;

    private String directors;

    @Enumerated(EnumType.STRING)
    private WatchStatus status = WatchStatus.PLANNED;
}