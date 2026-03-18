package com.tracker.mediatracker.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tracker.mediatracker.model.MediaItem;
import com.tracker.mediatracker.repo.MediaItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BackupService {

    private final MediaItemRepository repository;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .enable(SerializationFeature.INDENT_OUTPUT);

    public byte[] exportData() throws IOException {
        List<MediaItem> allItems = repository.findAll(Sort.by(Sort.Direction.ASC, "id"));

        return objectMapper
                .writerFor(new TypeReference<List<MediaItem>>() {
                })
                .writeValueAsBytes(allItems);
    }

    @Transactional
    public void importData(MultipartFile file, boolean clearBeforeImport) throws IOException {
        if (clearBeforeImport) {
            repository.deleteAll();
        }

        List<MediaItem> items = objectMapper.readValue(
                file.getInputStream(),
                new TypeReference<>() {
                }
        );

        items.forEach(item -> item.setId(null));

        repository.saveAll(items);
    }
}