package com.tracker.mediatracker.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.tracker.mediatracker.dto.TmdbDetailsDto;
import com.tracker.mediatracker.dto.TmdbSearchResultDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TmdbService {

    private final RestClient restClient;

    public TmdbService(
            @Value("${tmdb.api.url}") String apiUrl,
            @Value("${tmdb.api.token}") String token) {
        this.restClient = RestClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("Authorization", "Bearer " + token)
                .build();
    }

    public TmdbSearchResultDto search(String query) {
        log.debug("Searching TMDB for query: {}", query);
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/multi")
                        .queryParam("query", query)
                        .queryParam("language", "ru-RU")
                        .build())
                .retrieve()
                .body(TmdbSearchResultDto.class);
    }

    public TmdbDetailsDto getDetails(Long id, String mediaType) {
        log.debug("Fetching TMDB details for ID: {}, Type: {}", id, mediaType);
        TmdbDetailsDto dto = new TmdbDetailsDto();

        try {
            if ("movie".equalsIgnoreCase(mediaType)) {
                JsonNode response = restClient.get()
                        .uri("/movie/{id}?append_to_response=credits&language=ru-RU", id)
                        .retrieve()
                        .body(JsonNode.class);

                if (response != null) {
                    dto.setTitle(response.path("title").asText(null));
                    String date = response.path("release_date").asText("");
                    if (date.length() >= 4) dto.setReleaseYear(Integer.parseInt(date.substring(0, 4)));
                    dto.setDurationMinutes(response.path("runtime").asInt(0));
                    dto.setDirectors(extractDirectors(response.path("credits").path("crew")));
                }
            } else if ("tv".equalsIgnoreCase(mediaType)) {
                JsonNode response = restClient.get()
                        .uri("/tv/{id}?append_to_response=aggregate_credits&language=ru-RU", id)
                        .retrieve()
                        .body(JsonNode.class);

                if (response != null) {
                    dto.setTitle(response.path("name").asText(null));
                    String date = response.path("first_air_date").asText("");
                    if (date.length() >= 4) dto.setReleaseYear(Integer.parseInt(date.substring(0, 4)));
                    dto.setTotalEpisodes(response.path("number_of_episodes").asInt(0));

                    JsonNode runtimes = response.path("episode_run_time");
                    if (runtimes.isArray() && !runtimes.isEmpty()) {
                        int avgRuntime = runtimes.get(0).asInt(0);
                        dto.setDurationMinutes(dto.getTotalEpisodes() * avgRuntime);
                    }

                    dto.setDirectors(extractDirectors(response.path("aggregate_credits").path("crew")));
                }
            }
        } catch (Exception e) {
            log.error("Error fetching details from TMDB", e);
        }

        return dto;
    }

    private String extractDirectors(JsonNode crewNode) {
        if (!crewNode.isArray()) return null;
        List<String> directors = new ArrayList<>();
        for (JsonNode member : crewNode) {
            String job = member.path("job").asText("");
            if ("Director".equalsIgnoreCase(job) || "Creator".equalsIgnoreCase(job) || "Executive Producer".equalsIgnoreCase(job)) {
                directors.add(member.path("name").asText());
            }
        }
        return directors.isEmpty() ? null : String.join(", ", directors);
    }
}