package com.tracker.mediatracker.repo;

import com.tracker.mediatracker.model.WatchLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface WatchLogRepository extends JpaRepository<WatchLog, Long> {

    @Query(value = """
        SELECT TO_CHAR(d.date_series, 'YYYY-MM-DD') as watchDate, 
               COALESCE(SUM(w.minutes_watched), 0) as totalMinutes 
        FROM generate_series(CAST(:startDate AS timestamp), CURRENT_TIMESTAMP, interval '1 day') AS d(date_series)
        LEFT JOIN watch_log w ON DATE(w.watched_at) = DATE(d.date_series)
        GROUP BY d.date_series 
        ORDER BY d.date_series ASC
    """, nativeQuery = true)
    List<ChartDataProjection> getWatchActivitySince(@Param("startDate") LocalDateTime startDate);
}