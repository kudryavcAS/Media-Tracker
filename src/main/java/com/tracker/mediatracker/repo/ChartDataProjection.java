package com.tracker.mediatracker.repo;

public interface ChartDataProjection {
    String getWatchDate();
    Long getTotalMinutes();
    Long getMovieMinutes();
    Long getLiveActionMinutes();
    Long getAnimeMinutes();
    Long getAnimationMinutes();
}