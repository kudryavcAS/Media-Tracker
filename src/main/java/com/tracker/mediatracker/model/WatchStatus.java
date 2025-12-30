package com.tracker.mediatracker.model;

import lombok.Getter;

@Getter
public enum WatchStatus {
    PLANNED("Буду смотреть"),
    WATCHING("Смотрю"),
    COMPLETED("Просмотрено"),
    DROPPED("Брошено");

    private final String title;

    WatchStatus(String title) {
        this.title = title;
    }
}