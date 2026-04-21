package com.tracker.mediatracker.service;

import com.tracker.mediatracker.dto.MovieFormDto;
import com.tracker.mediatracker.dto.SeriesFormDto;
import com.tracker.mediatracker.dto.StatisticsDto;
import com.tracker.mediatracker.model.*;
import com.tracker.mediatracker.repo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaItemRepository mediaRepository;
    private final WatchLogRepository watchLogRepository;

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
        boolean isNew = true;
        boolean wasCompleted = false;

        if (dto.getId() != null) {
            movie = (Movie) mediaRepository.findById(dto.getId()).orElse(new Movie());
            if (movie.getId() != null) {
                isNew = false;
                wasCompleted = movie.getStatus() == WatchStatus.COMPLETED;
            }
        }

        dto.updateEntity(movie);

        if (!isNew && !wasCompleted && movie.getStatus() == WatchStatus.COMPLETED) {
            logWatchEvent(movie, 1);
        }

        mediaRepository.save(movie);
        log.debug("Movie saved successfully with ID: {}", movie.getId());
        return movie.getId();
    }

    @Transactional
    public Long saveSeries(SeriesFormDto dto) {
        log.info("Saving series: {}", dto.getTitle());
        Series series = new Series();
        boolean isNew = true;
        int oldWatched = 0;

        if (dto.getId() != null) {
            series = (Series) mediaRepository.findById(dto.getId()).orElse(new Series());
            if (series.getId() != null) {
                isNew = false;
                oldWatched = series.getWatchedEpisodes() == null ? 0 : series.getWatchedEpisodes();
            }
        }

        dto.updateEntity(series);
        series.syncState();

        if (!isNew) {
            int newWatched = series.getWatchedEpisodes() == null ? 0 : series.getWatchedEpisodes();
            if (newWatched > oldWatched) {
                logWatchEvent(series, newWatched - oldWatched);
            }
        }

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
                    int current = series.getWatchedEpisodes() == null ? 0 : series.getWatchedEpisodes();
                    int total = series.getTotalEpisodes() == null ? 0 : series.getTotalEpisodes();
                    int newVal = Math.max(0, current + change);
                    if (total > 0 && newVal > total) {
                        newVal = total;
                    }

                    int actualChange = newVal - current;
                    if (actualChange > 0) {
                        logWatchEvent(series, actualChange);
                    }

                    series.updateProgress(change);
                    mediaRepository.save(series);
                    log.debug("Progress updated. New watched episodes: {}", series.getWatchedEpisodes());
                });
    }

    @Transactional
    public void markAsCompleted(Long id) {
        log.info("Marking item with ID: {} as completed", id);
        mediaRepository.findById(id).ifPresent(item -> {
            if (item.getStatus() != WatchStatus.COMPLETED) {
                if (item instanceof Movie) {
                    logWatchEvent(item, 1);
                } else if (item instanceof Series series) {
                    int current = series.getWatchedEpisodes() == null ? 0 : series.getWatchedEpisodes();
                    int total = series.getTotalEpisodes() == null ? 1 : series.getTotalEpisodes();
                    if (current < total) {
                        logWatchEvent(series, total - current);
                    }
                }
                item.markAsCompleted();
                mediaRepository.save(item);
            }
        });
    }

    private void logWatchEvent(MediaItem item, int delta) {
        if (delta <= 0) return;

        int minutes = 0;
        String type = "MOVIE";

        if (item instanceof Movie) {
            minutes = item.getDurationMinutes() != null ? item.getDurationMinutes() : 0;
        } else if (item instanceof Series series) {
            type = series.getSeriesType() != null ? series.getSeriesType().name() : "SERIES";
            int totalDur = series.getDurationMinutes() != null ? series.getDurationMinutes() : 0;
            int totalEps = series.getTotalEpisodes() != null && series.getTotalEpisodes() > 0 ? series.getTotalEpisodes() : 1;
            minutes = Math.round((float) totalDur / totalEps * delta);
        }

        if (minutes > 0) {
            WatchLog watchLog = new WatchLog();
            watchLog.setTitle(item.getTitle());
            watchLog.setMediaType(type);
            watchLog.setMinutesWatched(minutes);
            watchLog.setWatchedAt(LocalDateTime.now());
            watchLogRepository.save(watchLog);
            log.info("Logged watch event: {} mins for '{}' [{}]", minutes, item.getTitle(), type);
        }
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
    
    public List<ChartDataProjection> getChartData(int days) {
        log.debug("Fetching chart data for the last {} days", days);
        LocalDateTime startDate = LocalDate.now().minusDays(days - 1).atStartOfDay();
        return watchLogRepository.getWatchActivitySince(startDate);
    }

}