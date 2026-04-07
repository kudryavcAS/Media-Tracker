package com.tracker.mediatracker.repo;

import com.tracker.mediatracker.model.MediaItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface MediaItemRepository extends JpaRepository<MediaItem, Long>, JpaSpecificationExecutor<MediaItem> {

    @Query(value = """
                SELECT 
                    COUNT(*) as totalItems,
                    COUNT(*) FILTER (WHERE content_type = 'MOVIE') as movieCount,
                    COUNT(*) FILTER (WHERE content_type = 'SERIES') as seriesCount,
                    COUNT(*) FILTER (WHERE status = 'COMPLETED') as completedCount,
                    COUNT(*) FILTER (WHERE status = 'WATCHING') as watchingCount,
                    COUNT(*) FILTER (WHERE status = 'PLANNED') as plannedCount,
                    COUNT(*) FILTER (WHERE status = 'DROPPED') as droppedCount,
                    COALESCE(SUM(duration_minutes), 0) as totalDuration,
            
                    COALESCE(SUM(
                        CASE 
                            WHEN status = 'COMPLETED' THEN COALESCE(duration_minutes, 0)
                            WHEN content_type = 'SERIES' AND total_episodes > 0 THEN (COALESCE(duration_minutes, 0) * COALESCE(watched_episodes, 0)) / total_episodes
                            ELSE 0
                        END
                    ), 0) as watchedDuration,
            
                    COALESCE(SUM(CASE WHEN content_type = 'MOVIE' AND status = 'COMPLETED' THEN COALESCE(duration_minutes, 0) ELSE 0 END), 0) as movieWatched,
            
                    COALESCE(SUM(CASE WHEN content_type = 'SERIES' AND series_type = 'LIVE_ACTION' THEN 
                        CASE WHEN status = 'COMPLETED' THEN COALESCE(duration_minutes, 0) 
                             WHEN total_episodes > 0 THEN (COALESCE(duration_minutes, 0) * COALESCE(watched_episodes, 0)) / total_episodes 
                             ELSE 0 END 
                        ELSE 0 END), 0) as liveActionWatched,
            
                    COALESCE(SUM(CASE WHEN content_type = 'SERIES' AND series_type = 'ANIME' THEN 
                        CASE WHEN status = 'COMPLETED' THEN COALESCE(duration_minutes, 0) 
                             WHEN total_episodes > 0 THEN (COALESCE(duration_minutes, 0) * COALESCE(watched_episodes, 0)) / total_episodes 
                             ELSE 0 END 
                        ELSE 0 END), 0) as animeWatched,
            
                    COALESCE(SUM(CASE WHEN content_type = 'SERIES' AND series_type = 'ANIMATION' THEN 
                        CASE WHEN status = 'COMPLETED' THEN COALESCE(duration_minutes, 0) 
                             WHEN total_episodes > 0 THEN (COALESCE(duration_minutes, 0) * COALESCE(watched_episodes, 0)) / total_episodes 
                             ELSE 0 END 
                        ELSE 0 END), 0) as animationWatched
                FROM media_item
            """, nativeQuery = true)
    StatsProjection getStats();
}