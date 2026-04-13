package com.tracker.mediatracker.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tracker.mediatracker.model.MediaItem;
import com.tracker.mediatracker.repo.MediaItemRepository;
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
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .enable(SerializationFeature.INDENT_OUTPUT);

    public byte[] exportData() throws IOException {
        log.info("Starting database export to JSON...");
        List<MediaItem> allItems = repository.findAll(Sort.by(Sort.Direction.ASC, "id"));

        byte[] data = objectMapper
                .writerFor(new TypeReference<List<MediaItem>>() {})
                .writeValueAsBytes(allItems);

        log.info("Successfully exported {} items.", allItems.size());
        return data;
    }

    @Transactional
    public void importData(MultipartFile file, boolean clearBeforeImport) throws IOException {
        log.info("Starting database import. Clear existing data: {}", clearBeforeImport);
        if (clearBeforeImport) {
            repository.deleteAllInBatch();
            log.debug("Database cleared successfully.");
        }

        List<MediaItem> items = objectMapper.readValue(
                file.getInputStream(),
                new TypeReference<>() {
                }
        );

        items.forEach(item -> item.setId(null));
        repository.saveAll(items);

        log.info("Successfully imported {} items.", items.size());
    }
}