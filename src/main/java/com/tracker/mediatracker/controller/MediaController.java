package com.tracker.mediatracker.controller;

import com.tracker.mediatracker.model.MediaItem;
import com.tracker.mediatracker.model.Movie;
import com.tracker.mediatracker.model.Series;
import com.tracker.mediatracker.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class MediaController {

    private final MediaService service;

    @GetMapping("/")
    public String index(@RequestParam(required = false, defaultValue = "ALL") String type,
                        @RequestParam(required = false, defaultValue = "ALL") String status,
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
    public String saveMovie(@ModelAttribute Movie movie) {
        service.save(movie);
        return "redirect:/";
    }

    @GetMapping("/add/series")
    public String showAddSeriesForm(Model model) {
        model.addAttribute("series", new Series());
        return "series_form";
    }

    @PostMapping("/save/series")
    public String saveSeries(@ModelAttribute Series series) {
        service.save(series);
        return "redirect:/";
    }

    @PostMapping("/series/{id}/inc")
    public String incrementSeries(@PathVariable Long id, @RequestHeader(value = "referer", required = false) String referer) {
        service.updateSeriesProgress(id, 1);
        return "redirect:" + (referer != null ? referer : "/");
    }

    @PostMapping("/series/{id}/dec")
    public String decrementSeries(@PathVariable Long id, @RequestHeader(value = "referer", required = false) String referer) {
        service.updateSeriesProgress(id, -1);
        return "redirect:" + (referer != null ? referer : "/");
    }

    @GetMapping("/edit/{id}")
    public String editItem(@PathVariable Long id, Model model) {
        MediaItem item = service.findById(id);
        switch (item) {
            case null -> {
                return "redirect:/";
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

        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String deleteItem(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/";
    }
}