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
        if (minutes == 0) return "0 мин.";
        long hours = minutes / 60;
        long mins = minutes % 60;
        long days = hours / 24;
        long remainingHours = hours % 24;

        if (days > 0) {
            return days + " дн. " + remainingHours + " ч. " + mins + " мин.";
        }
        return hours + " ч. " + mins + " мин.";
    }

    public String formatMinutes(long minutes) {
        return String.format(Locale.US, "%,d", minutes).replace(',', ' ') + " мин.";
    }

    public String formatHours(long minutes) {
        long hours = Math.round(minutes / 60.0);
        return "~" + hours + " ч.";
    }
}