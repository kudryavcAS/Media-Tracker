package com.tracker.mediatracker.controller;

import com.tracker.mediatracker.service.BackupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

@Controller
@RequestMapping("/backup")
@RequiredArgsConstructor
public class BackupController {

    private final BackupService backupService;

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportData() {
        try {
            byte[] data = backupService.exportData();
            String filename = "media_backup_" + LocalDate.now() + ".json";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(data);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/import")
    public String importData(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "redirect:/?error=empty_file";
        }
        try {
            backupService.importData(file);
            return "redirect:/?success=imported";
        } catch (IOException e) {
            return "redirect:/?error=import_failed";
        }
    }
}