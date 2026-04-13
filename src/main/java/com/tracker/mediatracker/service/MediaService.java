package com.tracker.mediatracker.service;

import com.tracker.mediatracker.dto.MovieFormDto;
import com.tracker.mediatracker.dto.SeriesFormDto;
import com.tracker.mediatracker.dto.StatisticsDto;
import com.tracker.mediatracker.model.MediaItem;
import com.tracker.mediatracker.model.Movie;
import com.tracker.mediatracker.model.Series;
import com.tracker.mediatracker.model.SortField;
import com.tracker.mediatracker.repo.MediaItemRepository;
import com.tracker.mediatracker.repo.MediaSpecifications;
import com.tracker.mediatracker.repo.StatsProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaItemRepository mediaRepository;

    public Page<MediaItem> getFilteredAndSortedItems(String typeFilter, String statusFilter, String sortParam, String query, int page, int size) {
        log.debug("Fetching items with filters - type: {}, status: {}, sort: {}, query: '{}', page: {}", typeFilter, statusFilter, sortParam, query, page);
        Specification<MediaItem> spec = MediaSpecifications.withFilters(typeFilter, statusFilter, query);
        Sort sort = createSort(sortParam);
        Pageable pageable = PageRequest.of(page, size, sort);
        return mediaRepository.findAll(spec, pageable);
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
    public Long saveMovie(MovieFormDto dto) {
        log.info("Saving movie: {}", dto.getTitle());
        Movie movie = new Movie();
        if (dto.getId() != null) {
            movie = (Movie) mediaRepository.findById(dto.getId()).orElse(new Movie());
        }
        dto.updateEntity(movie);
        mediaRepository.save(movie);
        log.debug("Movie saved successfully with ID: {}", movie.getId());
        return movie.getId();
    }

    @Transactional
    public Long saveSeries(SeriesFormDto dto) {
        log.info("Saving series: {}", dto.getTitle());
        Series series = new Series();
        if (dto.getId() != null) {
            series = (Series) mediaRepository.findById(dto.getId()).orElse(new Series());
        }
        dto.updateEntity(series);
        series.syncState();
        mediaRepository.save(series);
        log.debug("Series saved successfully with ID: {}", series.getId());
        return series.getId();
    }

    @Transactional
    public void delete(Long id) {
        log.warn("Deleting media item with ID: {}", id);
        mediaRepository.deleteById(id);
    }

    @Transactional
    public void updateSeriesProgress(Long id, int change) {
        log.info("Updating progress for series ID: {}, change: {}", id, change);
        mediaRepository.findById(id)
                .filter(Series.class::isInstance)
                .map(Series.class::cast)
                .ifPresent(series -> {
                    series.updateProgress(change);
                    mediaRepository.save(series);
                    log.debug("Progress updated. New watched episodes: {}", series.getWatchedEpisodes());
                });
    }

    @Transactional
    public void markAsCompleted(Long id) {
        log.info("Marking item with ID: {} as completed", id);
        mediaRepository.findById(id).ifPresent(item -> {
            item.markAsCompleted();
            mediaRepository.save(item);
        });
    }

    public StatisticsDto getStatistics() {
        log.debug("Calculating statistics...");
        StatsProjection proj = mediaRepository.getStats();
        StatisticsDto stats = new StatisticsDto();

        if (proj == null) {
            log.warn("Stats projection returned null.");
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