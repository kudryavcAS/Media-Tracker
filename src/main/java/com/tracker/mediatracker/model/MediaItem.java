package com.tracker.mediatracker.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "content_type")
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Movie.class, name = "MOVIE"),
        @JsonSubTypes.Type(value = Series.class, name = "SERIES")
})
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

    @Column(name = "content_type", insertable = false, updatable = false)
    private String contentType;

    @NotBlank(message = "Title cannot be empty")
    private String title;

    @NotNull(message = "Release year is required")
    @Min(value = 1888, message = "Year must be no earlier than 1888")
    private Integer releaseYear;

    @Min(value = 1, message = "Duration must be greater than 0")
    private Integer durationMinutes;

    private String directors;

    @Enumerated(EnumType.STRING)
    private WatchStatus status = WatchStatus.PLANNED;
}