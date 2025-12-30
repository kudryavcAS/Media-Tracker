package com.tracker.mediatracker.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("SERIES")
public class Series extends MediaItem {

    private Integer totalEpisodes;
    private Integer totalWatchedEpisodes = 0;

    @Enumerated(EnumType.STRING)
    private SeriesType seriesType;

    @OneToMany(mappedBy = "series", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Season> seasons = new ArrayList<>();
}