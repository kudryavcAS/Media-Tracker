package com.tracker.mediatracker.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracker.mediatracker.dto.BackupDto;
import com.tracker.mediatracker.model.MediaItem;
import com.tracker.mediatracker.model.WatchLog;
import com.tracker.mediatracker.repo.MediaItemRepository;
import com.tracker.mediatracker.repo.WatchLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BackupService {

    private final MediaItemRepository repository;
    private final WatchLogRepository watchLogRepository;
    private final ObjectMapper objectMapper;

    public byte[] exportData() {
        log.info("Starting database export to JSON...");
        List<MediaItem> items = repository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        List<WatchLog> logs = watchLogRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));

        try {
            byte[] data = objectMapper.writeValueAsBytes(new BackupDto(items, logs));
            log.info("Successfully exported {} items and {} watch logs.", items.size(), logs.size());
            return data;
        } catch (IOException e) {
            log.error("Error occurred while serializing backup data", e);
            throw new RuntimeException("Failed to export backup data", e);
        }
    }

    @Transactional
    public void importData(MultipartFile file, boolean clearBeforeImport) {
        log.info("Starting database import. Clear existing data: {}", clearBeforeImport);

        BackupDto backup;
        try {
            backup = parseBackup(file);
        } catch (IOException e) {
            log.error("Error occurred while reading backup file: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("Failed to parse backup file", e);
        }

        if (clearBeforeImport) {
            watchLogRepository.deleteAllInBatch();
            repository.deleteAllInBatch();
            log.debug("Database cleared successfully.");
        }

        List<MediaItem> items = backup.getMediaItems();
        if (!clearBeforeImport) {
            List<MediaItem> existing = repository.findAll();
            items.removeIf(newItem -> existing.stream()
                    .anyMatch(ex -> ex.getTitle().equalsIgnoreCase(newItem.getTitle())
                            && ex.getReleaseYear().equals(newItem.getReleaseYear())));
        }
        items.forEach(item -> item.setId(null));
        repository.saveAll(items);

        List<WatchLog> logs = backup.getWatchLogs();
        logs.forEach(log -> log.setId(null));
        watchLogRepository.saveAll(logs);

        log.info("Successfully imported {} items and {} watch logs.", items.size(), logs.size());
    }

    private BackupDto parseBackup(MultipartFile file) throws IOException {
        JsonNode root = objectMapper.readTree(file.getInputStream());

        if (root.isObject() && root.has("mediaItems")) {
            return objectMapper.treeToValue(root, BackupDto.class);
        }

        log.debug("Legacy backup format detected, importing media items only.");
        List<MediaItem> items = objectMapper.convertValue(root, new TypeReference<>() {});
        return new BackupDto(items, List.of());
    }
}