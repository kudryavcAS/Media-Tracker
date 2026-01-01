package com.tracker.mediatracker.repo;

import com.tracker.mediatracker.model.MediaItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MediaItemRepository extends JpaRepository<MediaItem, Long>, JpaSpecificationExecutor<MediaItem> {
}