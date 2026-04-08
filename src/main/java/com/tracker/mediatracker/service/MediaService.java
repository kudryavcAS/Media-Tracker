package com.tracker.mediatracker.service;

import com.tracker.mediatracker.dto.StatisticsDto;
import com.tracker.mediatracker.model.MediaItem;
import com.tracker.mediatracker.model.Series;
import com.tracker.mediatracker.model.SortField;
import com.tracker.mediatracker.repo.MediaItemRepository;
import com.tracker.mediatracker.repo.MediaSpecifications;
import com.tracker.mediatracker.repo.StatsProjection;
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
        Sort primarySort = switch (field) {
            case ID -> Sort.by(Sort.Direction.ASC, field.getEntityFieldName());
            case YEAR, DURATION -> Sort.by(Sort.Direction.DESC, field.getEntityFieldName());
            default -> Sort.by(field.getEntityFieldName());
        };

        if (field != SortField.ID) {
            return primarySort.and(Sort.by(Sort.Direction.ASC, "id"));
        }
        return primarySort;
    }

    public MediaItem findById(Long id) {
        return mediaRepository.findById(id).orElse(null);
    }

    @Transactional
    public void save(MediaItem item) {
        if (item instanceof Series series) {
            series.syncState();
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
                    series.updateProgress(change);
                    mediaRepository.save(series);
                });
    }

    @Transactional
    public void markAsCompleted(Long id) {
        mediaRepository.findById(id).ifPresent(item -> {
            item.markAsCompleted();
            mediaRepository.save(item);
        });
    }

    public StatisticsDto getStatistics() {
        StatsProjection proj = mediaRepository.getStats();
        StatisticsDto stats = new StatisticsDto();

        if (proj == null) {
            return stats;
        }

        stats.setTotalItems(proj.getTotalItems());
        stats.setMovieCount(proj.getMovieCount());
        stats.setSeriesCount(proj.getSeriesCount());
        stats.setCompletedCount(proj.getCompletedCount());
        stats.setWatchingCount(proj.getWatchingCount());
        stats.setPlannedCount(proj.getPlannedCount());
        stats.setDroppedCount(proj.getDroppedCount());
        stats.setTotalDurationMinutes(proj.getTotalDuration());
        stats.setWatchedDurationMinutes(proj.getWatchedDuration());
        stats.setMovieWatchedMinutes(proj.getMovieWatched());
        stats.setLiveActionWatchedMinutes(proj.getLiveActionWatched());
        stats.setAnimeWatchedMinutes(proj.getAnimeWatched());
        stats.setAnimationWatchedMinutes(proj.getAnimationWatched());

        return stats;
    }
}