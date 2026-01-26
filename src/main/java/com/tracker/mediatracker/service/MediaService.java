package com.tracker.mediatracker.service;

import com.tracker.mediatracker.model.MediaItem;
import com.tracker.mediatracker.model.Series;
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
            case ID, YEAR, DURATION -> Sort.by(Sort.Direction.DESC, field.getEntityFieldName());
            default -> Sort.by(field.getEntityFieldName());
        };
    }

    public MediaItem findById(Long id) {
        return mediaRepository.findById(id).orElse(null);
    }

    @Transactional
    public void save(MediaItem item) {
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
                    int newVal = Math.max(0, current + change);

                    if (series.getTotalEpisodes() != null && newVal >= series.getTotalEpisodes()) {
                        newVal = series.getTotalEpisodes();
                        series.setStatus(WatchStatus.COMPLETED);
                    }

                    series.setWatchedEpisodes(newVal);
                    mediaRepository.save(series);
                });
    }
}