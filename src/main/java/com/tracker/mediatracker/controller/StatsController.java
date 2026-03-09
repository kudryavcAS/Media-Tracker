package com.tracker.mediatracker.controller;

import com.tracker.mediatracker.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class StatsController {

    private final MediaService mediaService;

    @GetMapping("/stats")
    public String showStats(Model model) {
        model.addAttribute("stats", mediaService.getStatistics());
        return "stats";
    }
}