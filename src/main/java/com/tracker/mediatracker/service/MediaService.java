package com.tracker.mediatracker.service;

import com.tracker.mediatracker.model.MediaItem;
import com.tracker.mediatracker.model.Season;
import com.tracker.mediatracker.model.WatchStatus;
import com.tracker.mediatracker.repo.MediaItemRepository;
import com.tracker.mediatracker.repo.SeasonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaItemRepository mediaRepository;
    private final SeasonRepository seasonRepository;

    public List<MediaItem> findAll() {
        return mediaRepository.findAllByOrderByReleaseDateDesc();
    }

    public List<MediaItem> findByStatus(WatchStatus status) {
        return mediaRepository.findByStatus(status);
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