package com.tracker.mediatracker.repo;

import com.tracker.mediatracker.model.MediaItem;
import com.tracker.mediatracker.model.SeriesType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MediaItemRepository extends JpaRepository<MediaItem, Long> {

    @Query("SELECT m FROM Movie m")
    List<MediaItem> findAllMovies();

    @Query("SELECT s FROM Series s")
    List<MediaItem> findAllSeries();

    @Query("SELECT s FROM Series s WHERE s.seriesType = :type")
    List<MediaItem> findAllSeriesByType(@Param("type") SeriesType type);
}