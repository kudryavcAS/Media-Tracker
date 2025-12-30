package com.tracker.mediatracker.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Data
@Entity
public class Season {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer seasonNumber;
    private Integer episodeCount;
    private Integer watchedEpisodes = 0;

    @ManyToOne
    @JoinColumn(name = "series_id")
    @ToString.Exclude
    private Series series;
}