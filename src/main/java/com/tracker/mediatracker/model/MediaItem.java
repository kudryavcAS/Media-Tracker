package com.tracker.mediatracker.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "content_type")
public abstract class MediaItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private LocalDate releaseDate;
    private Integer durationMinutes;
    private String directors;

    @Enumerated(EnumType.STRING)
    private WatchStatus status = WatchStatus.PLANNED;
}