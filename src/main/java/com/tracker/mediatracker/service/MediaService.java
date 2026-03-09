package com.tracker.mediatracker.service;

import com.tracker.mediatracker.dto.StatisticsDto;
import com.tracker.mediatracker.model.MediaItem;
import com.tracker.mediatracker.model.Movie;
import com.tracker.mediatracker.model.Series;
import com.tracker.mediatracker.model.SeriesType;
import com.tracker.mediatracker.model.SortField;
import com.tracker.mediatracker.model.WatchStatus;
import com.tracker.mediatracker.repo.MediaItemRepository;
import com.tracker.mediatracker.repo.MediaSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaItemRepository mediaRepository;

    public List<MediaItem> getFilteredAndSortedItems(String typeFilter, String statusFilter, String sortParam, String query) {
        Specification<MediaItem> spec = MediaSpecifications.withFilters(typeFilter, statusFilter, query);
        Sort sort = createSort(sortParam);
        return mediaRepository.findAll(spec, sort);
    }

    private Sort createSort(String sortParam) {
        SortField field = SortField.fromString(sortParam);
        return switch (field) {
            case ID -> Sort.by(Sort.Direction.ASC, field.getEntityFieldName());
            case YEAR, DURATION -> Sort.by(Sort.Direction.DESC, field.getEntityFieldName());
            default -> Sort.by(field.getEntityFieldName());
        };
    }

    public MediaItem findById(Long id) {
        return mediaRepository.findById(id).orElse(null);
    }

    @Transactional
    public void save(MediaItem item) {
        if (item instanceof Series series) {
            int watched = series.getWatchedEpisodes() == null ? 0 : series.getWatchedEpisodes();
            int total = series.getTotalEpisodes() == null ? 0 : series.getTotalEpisodes();

            if (total > 0 && watched >= total) {
                series.setWatchedEpisodes(total);
                series.setStatus(WatchStatus.COMPLETED);
            } else if (watched > 0 && series.getStatus() == WatchStatus.PLANNED) {
                series.setStatus(WatchStatus.WATCHING);
            } else if (watched == 0 && series.getStatus() == WatchStatus.WATCHING) {
                series.setStatus(WatchStatus.PLANNED);
            }
        }
        mediaRepository.save(item);
    }

    @Transactional
    public void delete(Long id) {
        mediaRepository.deleteById(id);
    }

    @Transactional
    public void updateSeriesProgress(Long id, int change) {
        mediaRepository.findById(id)
                .filter(Series.class::isInstance)
                .map(Series.class::cast)
                .ifPresent(series -> {
                    int current = series.getWatchedEpisodes() == null ? 0 : series.getWatchedEpisodes();
                    int total = series.getTotalEpisodes() == null ? 0 : series.getTotalEpisodes();
                    int newVal = Math.max(0, current + change);

                    if (total > 0 && newVal >= total) {
                        newVal = total;
                    }

                    series.setWatchedEpisodes(newVal);

                    if (series.getStatus() == WatchStatus.COMPLETED && newVal < total) {
                        series.setStatus(WatchStatus.WATCHING);
                    }

                    save(series);
                });
    }

    @Transactional
    public void markAsCompleted(Long id) {
        mediaRepository.findById(id).ifPresent(item -> {
            item.setStatus(WatchStatus.COMPLETED);
            if (item instanceof Series series) {
                if (series.getTotalEpisodes() != null) {
                    series.setWatchedEpisodes(series.getTotalEpisodes());
                }
            }
            mediaRepository.save(item);
        });
    }

    public StatisticsDto getStatistics() {
        List<MediaItem> items = mediaRepository.findAll();
        StatisticsDto stats = new StatisticsDto();

        stats.setTotalItems(items.size());

        long totalDurationAll = 0;
        long watchedDuration = 0;

        long movieCount = 0;
        long seriesCount = 0;

        long movieWatchedTime = 0;
        long liveActionWatchedTime = 0;
        long animeWatchedTime = 0;
        long animationWatchedTime = 0;

        long completedCount = 0;
        long watchingCount = 0;
        long plannedCount = 0;
        long droppedCount = 0;

        for (MediaItem item : items) {
            long itemTotalTime = item.getDurationMinutes() != null ? item.getDurationMinutes() : 0;
            totalDurationAll += itemTotalTime;

            long itemWatchedTime = 0;

            switch (item.getStatus()) {
                case COMPLETED -> completedCount++;
                case WATCHING -> watchingCount++;
                case PLANNED -> plannedCount++;
                case DROPPED -> droppedCount++;
            }

            if (item instanceof Movie) {
                movieCount++;
                if (item.getStatus() == WatchStatus.COMPLETED) {
                    itemWatchedTime = itemTotalTime;
                    movieWatchedTime += itemWatchedTime;
                }
            } else if (item instanceof Series series) {
                seriesCount++;
                int watchedEps = series.getWatchedEpisodes() != null ? series.getWatchedEpisodes() : 0;
                int totalEps = series.getTotalEpisodes() != null && series.getTotalEpisodes() > 0 ? series.getTotalEpisodes() : 1;

                if (series.getStatus() == WatchStatus.COMPLETED) {
                    itemWatchedTime = itemTotalTime;
                } else {
                    itemWatchedTime = (long) ((double) itemTotalTime / totalEps * watchedEps);
                }

                if (series.getSeriesType() == SeriesType.LIVE_ACTION) liveActionWatchedTime += itemWatchedTime;
                if (series.getSeriesType() == SeriesType.ANIME) animeWatchedTime += itemWatchedTime;
                if (series.getSeriesType() == SeriesType.ANIMATION) animationWatchedTime += itemWatchedTime;
            }
            watchedDuration += itemWatchedTime;
        }

        stats.setTotalDurationMinutes(totalDurationAll);
        stats.setWatchedDurationMinutes(watchedDuration);
        stats.setMovieCount(movieCount);
        stats.setSeriesCount(seriesCount);
        stats.setMovieWatchedMinutes(movieWatchedTime);
        stats.setLiveActionWatchedMinutes(liveActionWatchedTime);
        stats.setAnimeWatchedMinutes(animeWatchedTime);
        stats.setAnimationWatchedMinutes(animationWatchedTime);
        stats.setCompletedCount(completedCount);
        stats.setWatchingCount(watchingCount);
        stats.setPlannedCount(plannedCount);
        stats.setDroppedCount(droppedCount);

        return stats;
    }
}