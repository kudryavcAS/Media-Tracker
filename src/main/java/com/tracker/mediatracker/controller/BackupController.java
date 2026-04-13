package com.tracker.mediatracker.controller;

import com.tracker.mediatracker.service.BackupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Controller
@RequestMapping("/backup")
@RequiredArgsConstructor
public class BackupController {

    private final BackupService backupService;

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportData() {

        byte[] data = backupService.exportData();
        String filename = "media_backup_" + LocalDate.now() + ".json";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_JSON)
                .body(data);
    }

    @PostMapping("/import")
    public String importData(@RequestParam("file") MultipartFile file,
                             @RequestParam(value = "mode", defaultValue = "append") String mode) {
        if (file.isEmpty()) {
            return "redirect:/?error=empty_file";
        }

        boolean clear = "overwrite".equals(mode);
        backupService.importData(file, clear);
        return "redirect:/?success=imported";
    }
}