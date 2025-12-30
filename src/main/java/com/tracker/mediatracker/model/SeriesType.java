package com.tracker.mediatracker.model;

import lombok.Getter;

@Getter
public enum SeriesType {
    LIVE_ACTION("Сериал"),
    ANIME("Аниме"),
    ANIMATION("Мультфильм");

    private final String title;

    SeriesType(String title) {
        this.title = title;
    }
}