package com.tracker.mediatracker.controller;

import com.tracker.mediatracker.model.MediaItem;
import com.tracker.mediatracker.model.Movie;
import com.tracker.mediatracker.model.Series;
import com.tracker.mediatracker.service.MediaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class MediaController {

    private static final String REDIRECT_HOME = "redirect:/";
    private static final String FILTER_ALL = "ALL";

    private final MediaService service;

    @GetMapping("/")
    public String index(@RequestParam(required = false, defaultValue = FILTER_ALL) String type,
                        @RequestParam(required = false, defaultValue = FILTER_ALL) String status,
                        @RequestParam(required = false) String sort,
                        @RequestParam(required = false) String q,
                        Model model) {

        model.addAttribute("items", service.getFilteredAndSortedItems(type, status, sort, q));

        model.addAttribute("currentType", type);
        model.addAttribute("currentStatus", status);
        model.addAttribute("currentSort", sort);
        model.addAttribute("currentQuery", q);

        return "index";
    }

    @GetMapping("/add/movie")
    public String showAddMovieForm(Model model) {
        model.addAttribute("movie", new Movie());
        return "movie_form";
    }

    @PostMapping("/save/movie")
    public String saveMovie(@Valid @ModelAttribute Movie movie, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "movie_form";
        }
        service.save(movie);
        return REDIRECT_HOME;
    }

    @GetMapping("/add/series")
    public String showAddSeriesForm(Model model) {
        model.addAttribute("series", new Series());
        return "series_form";
    }

    @PostMapping("/save/series")
    public String saveSeries(@Valid @ModelAttribute Series series, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "series_form";
        }
        service.save(series);
        return REDIRECT_HOME;
    }

    @PostMapping("/series/{id}/inc")
    public String incrementSeries(@PathVariable Long id, @RequestHeader(value = "referer", required = false) String referer) {
        service.updateSeriesProgress(id, 1);
        return getSafeRedirect(referer);
    }

    @PostMapping("/series/{id}/dec")
    public String decrementSeries(@PathVariable Long id, @RequestHeader(value = "referer", required = false) String referer) {
        service.updateSeriesProgress(id, -1);
        return getSafeRedirect(referer);
    }

    @PostMapping("/item/{id}/complete")
    public String markAsCompleted(@PathVariable Long id, @RequestHeader(value = "referer", required = false) String referer) {
        service.markAsCompleted(id);
        return getSafeRedirect(referer);
    }

    @GetMapping("/edit/{id}")
    public String editItem(@PathVariable Long id, Model model) {
        MediaItem item = service.findById(id);
        switch (item) {
            case null -> {
                return REDIRECT_HOME;
            }
            case Movie movie -> {
                model.addAttribute("movie", movie);
                return "movie_form";
            }
            case Series series -> {
                model.addAttribute("series", series);
                return "series_form";
            }
            default -> {
            }
        }

        return REDIRECT_HOME;
    }

    @GetMapping("/delete/{id}")
    public String deleteItem(@PathVariable Long id) {
        service.delete(id);
        return REDIRECT_HOME;
    }

    @GetMapping("/settings")
    public String showSettingsPage() {
        return "settings";
    }

    private String getSafeRedirect(String referer) {
        if (referer != null && (referer.startsWith("/") || referer.contains("localhost"))) {
            return "redirect:" + referer;
        }
        return REDIRECT_HOME;
    }
}