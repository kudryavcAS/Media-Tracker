package com.tracker.mediatracker.controller;

import com.tracker.mediatracker.service.MediaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class StatsController {

    private final MediaService mediaService;

    @GetMapping("/stats")
    public String showStats(Model model) {
        log.debug("Accessing statistics page...");
        model.addAttribute("stats", mediaService.getStatistics());
        return "stats";
    }
}