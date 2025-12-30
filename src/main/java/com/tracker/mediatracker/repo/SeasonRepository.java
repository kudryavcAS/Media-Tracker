package com.tracker.mediatracker.repo;

import com.tracker.mediatracker.model.Season;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeasonRepository extends JpaRepository<Season, Long> {
}