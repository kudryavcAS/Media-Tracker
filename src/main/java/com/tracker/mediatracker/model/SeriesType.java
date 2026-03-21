package com.tracker.mediatracker.model;

import lombok.Getter;

@Getter
public enum SeriesType {
    LIVE_ACTION("Live Action"),
    ANIME("Anime"),
    ANIMATION("Animation");

    private final String title;

    SeriesType(String title) {
        this.title = title;
    }
}