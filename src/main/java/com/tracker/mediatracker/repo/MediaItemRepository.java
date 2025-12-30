package com.tracker.mediatracker.repo;

import com.tracker.mediatracker.model.MediaItem;
import com.tracker.mediatracker.model.WatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MediaItemRepository extends JpaRepository<MediaItem, Long> {

    List<MediaItem> findByStatus(WatchStatus status);

    List<MediaItem> findByTitleContainingIgnoreCase(String title);

    List<MediaItem> findAllByOrderByReleaseDateDesc();

    @Query("SELECT m FROM Movie m")
    List<MediaItem> findAllMovies();

    @Query("SELECT s FROM Series s")
    List<MediaItem> findAllSeries();
}