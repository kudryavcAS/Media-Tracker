package com.tracker.mediatracker.service;

import com.tracker.mediatracker.model.MediaItem;
import com.tracker.mediatracker.model.Season;
import com.tracker.mediatracker.model.SeriesType;
import com.tracker.mediatracker.model.WatchStatus;
import com.tracker.mediatracker.repo.MediaItemRepository;
import com.tracker.mediatracker.repo.SeasonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaItemRepository mediaRepository;
    private final SeasonRepository seasonRepository;

    public List<MediaItem> getFilteredAndSortedItems(String typeFilter, String statusFilter, String sort) {
        List<MediaItem> items = fetchItemsByType(typeFilter);
        items = filterByStatus(items, statusFilter);
        applySorting(items, sort);
        return items;
    }

    private List<MediaItem> fetchItemsByType(String typeFilter) {
        String filter = typeFilter == null ? "ALL" : typeFilter;

        return switch (filter) {
            case "ALL" -> mediaRepository.findAll();
            case "MOVIE" -> mediaRepository.findAllMovies();
            case "SERIES" -> mediaRepository.findAllSeries();
            default -> {
                try {
                    yield mediaRepository.findAllSeriesByType(SeriesType.valueOf(filter));
                } catch (IllegalArgumentException e) {
                    yield mediaRepository.findAll();
                }
            }
        };
    }

    private List<MediaItem> filterByStatus(List<MediaItem> items, String statusFilter) {
        if (statusFilter == null || statusFilter.equals("ALL")) {
            return items;
        }
        try {
            WatchStatus status = WatchStatus.valueOf(statusFilter);
            return items.stream()
                    .filter(item -> item.getStatus() == status)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            return items;
        }
    }

    private void applySorting(List<MediaItem> items, String sort) {
        if (sort == null) {
            items.sort(Comparator.comparing(MediaItem::getId).reversed());
            return;
        }

        switch (sort) {
            case "title" -> items.sort(Comparator.comparing(MediaItem::getTitle));
            case "year" -> items.sort(Comparator.comparing(MediaItem::getReleaseYear, Comparator.nullsLast(Comparator.reverseOrder())));
            case "duration" -> items.sort(Comparator.comparing(MediaItem::getDurationMinutes, Comparator.nullsLast(Comparator.reverseOrder())));
            case "director" -> items.sort(Comparator.comparing(MediaItem::getDirectors, Comparator.nullsLast(Comparator.naturalOrder())));
            case "type" -> items.sort(Comparator.comparing(item -> item.getClass().getSimpleName()));
            default -> items.sort(Comparator.comparing(MediaItem::getId).reversed());
        }
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
    public void saveSeason(Season season) {
        seasonRepository.save(season);
    }
}