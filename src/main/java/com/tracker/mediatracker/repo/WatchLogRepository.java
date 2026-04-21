package com.tracker.mediatracker.repo;

import com.tracker.mediatracker.model.WatchLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface WatchLogRepository extends JpaRepository<WatchLog, Long> {

    @Query(value = """
        SELECT TO_CHAR(watched_at, 'YYYY-MM-DD') as watchDate, 
               COALESCE(SUM(minutes_watched), 0) as totalMinutes 
        FROM watch_log 
        WHERE watched_at >= :startDate 
        GROUP BY TO_CHAR(watched_at, 'YYYY-MM-DD') 
        ORDER BY watchDate ASC
    """, nativeQuery = true)
    List<ChartDataProjection> getWatchActivitySince(@Param("startDate") LocalDateTime startDate);
}