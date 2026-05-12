package com.tracker.mediatracker.dto;

import com.tracker.mediatracker.model.MediaItem;
import com.tracker.mediatracker.model.WatchLog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BackupDto {
    private List<MediaItem> mediaItems;
    private List<WatchLog> watchLogs;
}