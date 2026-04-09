package com.tracker.mediatracker.controller;

import com.tracker.mediatracker.dto.MovieFormDto;
import com.tracker.mediatracker.dto.SeriesFormDto;
import com.tracker.mediatracker.model.MediaItem;
import com.tracker.mediatracker.model.Movie;
import com.tracker.mediatracker.model.Series;
import com.tracker.mediatracker.service.MediaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "50") int size,
                        Model model) {

        Page<MediaItem> itemPage = service.getFilteredAndSortedItems(type, status, sort, q, page, size);

        model.addAttribute("items", itemPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", itemPage.getTotalPages());

        model.addAttribute("currentType", type);
        model.addAttribute("currentStatus", status);
        model.addAttribute("currentSort", sort);
        model.addAttribute("currentQuery", q);

        return "index";
    }

    @GetMapping("/add/movie")
    public String showAddMovieForm(Model model) {
        model.addAttribute("movie", new MovieFormDto());
        return "movie_form";
    }

    @PostMapping("/save/movie")
    public String saveMovie(@Valid @ModelAttribute("movie") MovieFormDto movieDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "movie_form";
        }
        Long id = service.saveMovie(movieDto);
        return "redirect:/#main-row-" + id;
    }

    @GetMapping("/add/series")
    public String showAddSeriesForm(Model model) {
        model.addAttribute("series", new SeriesFormDto());
        return "series_form";
    }

    @PostMapping("/save/series")
    public String saveSeries(@Valid @ModelAttribute("series") SeriesFormDto seriesDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "series_form";
        }
        Long id = service.saveSeries(seriesDto);
        return "redirect:/#main-row-" + id;
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
                model.addAttribute("movie", MovieFormDto.fromEntity(movie));
                return "movie_form";
            }
            case Series series -> {
                model.addAttribute("series", SeriesFormDto.fromEntity(series));
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