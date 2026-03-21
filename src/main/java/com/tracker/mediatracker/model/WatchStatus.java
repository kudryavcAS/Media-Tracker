package com.tracker.mediatracker.model;

import lombok.Getter;

@Getter
public enum WatchStatus {
    PLANNED("Planned"),
    WATCHING("Watching"),
    COMPLETED("Completed"),
    DROPPED("Dropped");

    private final String title;

    WatchStatus(String title) {
        this.title = title;
    }
}