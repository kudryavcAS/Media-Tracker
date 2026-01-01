package com.tracker.mediatracker.controller;

import com.tracker.mediatracker.model.Movie;
import com.tracker.mediatracker.model.Series;
import com.tracker.mediatracker.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class MediaController {

    private final MediaService service;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("items", service.findAll());
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
}