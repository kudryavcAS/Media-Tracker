package com.tracker.mediatracker.repo;

import com.tracker.mediatracker.model.WatchLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WatchLogRepository extends JpaRepository<WatchLog, Long> {
}