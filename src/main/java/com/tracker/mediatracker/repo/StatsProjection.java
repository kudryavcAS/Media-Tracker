package com.tracker.mediatracker.repo;

public interface StatsProjection {
    long getTotalItems();

    long getMovieCount();

    long getSeriesCount();

    long getCompletedCount();

    long getWatchingCount();

    long getPlannedCount();

    long getDroppedCount();

    long getTotalDuration();

    long getWatchedDuration();

    long getMovieWatched();

    long getLiveActionWatched();

    long getAnimeWatched();

    long getAnimationWatched();
}