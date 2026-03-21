package com.tracker.mediatracker.dto;

import lombok.Data;

import java.util.Locale;

@Data
public class StatisticsDto {

    private long totalItems;
    private long movieCount;
    private long seriesCount;

    private long completedCount;
    private long watchingCount;
    private long plannedCount;
    private long droppedCount;

    private long totalDurationMinutes;
    private long watchedDurationMinutes;

    private long movieWatchedMinutes;
    private long liveActionWatchedMinutes;
    private long animeWatchedMinutes;
    private long animationWatchedMinutes;

    public String formatTime(long minutes) {
        if (minutes == 0) return "0 min.";
        long hours = minutes / 60;
        long mins = minutes % 60;
        long days = hours / 24;
        long remainingHours = hours % 24;

        if (days > 0) {
            return days + " d. " + remainingHours + " h. " + mins + " min.";
        }
        return hours + " h. " + mins + " min.";
    }

    public String formatMinutes(long minutes) {
        return String.format(Locale.US, "%,d", minutes).replace(',', ' ') + " min.";
    }

    public String formatHours(long minutes) {
        long hours = Math.round(minutes / 60.0);
        return "~" + hours + " h.";
    }
}