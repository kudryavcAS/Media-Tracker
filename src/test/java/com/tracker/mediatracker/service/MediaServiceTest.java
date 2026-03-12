package com.tracker.mediatracker.service;

import com.tracker.mediatracker.dto.StatisticsDto;
import com.tracker.mediatracker.model.Movie;
import com.tracker.mediatracker.model.Series;
import com.tracker.mediatracker.model.SeriesType;
import com.tracker.mediatracker.model.WatchStatus;
import com.tracker.mediatracker.repo.MediaItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MediaServiceTest {

    @Mock
    private MediaItemRepository mediaRepository;

    @InjectMocks
    private MediaService mediaService;

    @Test
    void shouldChangeStatusToCompleted_WhenAllEpisodesWatched() {
        Series series = new Series();
        series.setId(1L);
        series.setTotalEpisodes(10);
        series.setWatchedEpisodes(9);
        series.setStatus(WatchStatus.WATCHING);

        when(mediaRepository.findById(1L)).thenReturn(Optional.of(series));

        mediaService.updateSeriesProgress(1L, 1);

        assertThat(series.getWatchedEpisodes()).isEqualTo(10);
        assertThat(series.getStatus()).isEqualTo(WatchStatus.COMPLETED);
    }

    @Test
    void shouldChangeStatusToWatching_WhenFirstEpisodeWatched() {
        Series series = new Series();
        series.setId(2L);
        series.setTotalEpisodes(24);
        series.setWatchedEpisodes(0);
        series.setStatus(WatchStatus.PLANNED);

        when(mediaRepository.findById(2L)).thenReturn(Optional.of(series));

        mediaService.updateSeriesProgress(2L, 1);

        assertThat(series.getWatchedEpisodes()).isEqualTo(1);
        assertThat(series.getStatus()).isEqualTo(WatchStatus.WATCHING);
    }

    @Test
    void shouldNotExceedTotalEpisodes() {
        Series series = new Series();
        series.setId(3L);
        series.setTotalEpisodes(5);
        series.setWatchedEpisodes(5);
        series.setStatus(WatchStatus.COMPLETED);

        when(mediaRepository.findById(3L)).thenReturn(Optional.of(series));

        mediaService.updateSeriesProgress(3L, 1);

        assertThat(series.getWatchedEpisodes()).isEqualTo(5);
    }

    @Test
    void shouldCalculateStatisticsCorrectly() {
        Movie movie1 = new Movie();
        movie1.setDurationMinutes(120);
        movie1.setStatus(WatchStatus.COMPLETED);

        Movie movie2 = new Movie();
        movie2.setDurationMinutes(90);
        movie2.setStatus(WatchStatus.PLANNED);

        Series series1 = new Series();
        series1.setTotalEpisodes(10);
        series1.setWatchedEpisodes(5);
        series1.setDurationMinutes(500);
        series1.setSeriesType(SeriesType.LIVE_ACTION);
        series1.setStatus(WatchStatus.WATCHING);

        when(mediaRepository.findAll()).thenReturn(List.of(movie1, movie2, series1));

        StatisticsDto stats = mediaService.getStatistics();

        assertThat(stats.getTotalItems()).isEqualTo(3);
        assertThat(stats.getMovieCount()).isEqualTo(2);
        assertThat(stats.getSeriesCount()).isEqualTo(1);

        assertThat(stats.getCompletedCount()).isEqualTo(1);
        assertThat(stats.getWatchingCount()).isEqualTo(1);
        assertThat(stats.getPlannedCount()).isEqualTo(1);

        assertThat(stats.getTotalDurationMinutes()).isEqualTo(710);
        assertThat(stats.getWatchedDurationMinutes()).isEqualTo(370);
    }
}