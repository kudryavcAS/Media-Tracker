package com.tracker.mediatracker.controller;

import com.tracker.mediatracker.dto.TmdbDetailsDto;
import com.tracker.mediatracker.dto.TmdbSearchResultDto;
import com.tracker.mediatracker.service.TmdbService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tmdb")
@RequiredArgsConstructor
public class TmdbRestController {

    private final TmdbService tmdbService;

    @GetMapping("/search")
    public TmdbSearchResultDto search(@RequestParam String query) {
        return tmdbService.search(query);
    }

    @GetMapping("/details")
    public TmdbDetailsDto getDetails(@RequestParam Long id, @RequestParam String type) {
        return tmdbService.getDetails(id, type);
    }
}