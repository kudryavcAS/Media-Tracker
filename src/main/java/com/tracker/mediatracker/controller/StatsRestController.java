package com.tracker.mediatracker.controller;

import com.tracker.mediatracker.repo.ChartDataProjection;
import com.tracker.mediatracker.service.MediaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsRestController {

    private final MediaService mediaService;

    @GetMapping("/chart")
    public List<ChartDataProjection> getChartData(@RequestParam(defaultValue = "14") int days) {
        return mediaService.getChartData(days);
    }
}