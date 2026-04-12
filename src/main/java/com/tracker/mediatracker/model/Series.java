package com.tracker.mediatracker.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("SERIES")
public class Series extends MediaItem {

    private Integer totalEpisodes;

    private Integer watchedEpisodes = 0;

    @Enumerated(EnumType.STRING)
    private SeriesType seriesType;

    @Override
    public void markAsCompleted() {
        super.markAsCompleted();
        if (this.totalEpisodes != null && this.totalEpisodes > 0) {
            this.watchedEpisodes = this.totalEpisodes;
        }
    }

    public void updateProgress(int change) {
        int current = this.watchedEpisodes == null ? 0 : this.watchedEpisodes;
        int total = this.totalEpisodes == null ? 0 : this.totalEpisodes;
        int newVal = Math.max(0, current + change);

        if (total > 0 && newVal >= total) {
            newVal = total;
        }

        if (change > 0 && this.getStatus() == WatchStatus.DROPPED && newVal > current) {
            this.setStatus(WatchStatus.WATCHING);
        }

        this.watchedEpisodes = newVal;
        syncState();
    }

    public void syncState() {
        int watched = this.watchedEpisodes == null ? 0 : this.watchedEpisodes;
        int total = this.totalEpisodes == null ? 0 : this.totalEpisodes;

        if (total > 0 && watched >= total) {
            this.watchedEpisodes = total;
            this.setStatus(WatchStatus.COMPLETED);
        } else if (watched > 0 && this.getStatus() == WatchStatus.PLANNED) {
            this.setStatus(WatchStatus.WATCHING);
        } else if (watched == 0 && this.getStatus() == WatchStatus.WATCHING) {
            this.setStatus(WatchStatus.PLANNED);
        } else if (this.getStatus() == WatchStatus.COMPLETED && watched < total) {
            this.setStatus(WatchStatus.WATCHING);
        }
    }
}