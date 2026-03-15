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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MediaServiceTest {

    @Mock
    private MediaItemRepository mediaRepository;

    @InjectMocks
    private MediaService mediaService;

    @Test
    void save_Movie_ShouldNotAlterStatus() {
        Movie movie = new Movie();
        movie.setId(1L);
        movie.setStatus(WatchStatus.PLANNED);

        mediaService.save(movie);

        assertThat(movie.getStatus()).isEqualTo(WatchStatus.PLANNED);
        verify(mediaRepository, times(1)).save(movie);
    }

    @Test
    void save_Series_WhenWatchedEqualsTotal_ShouldSetCompleted() {
        Series series = new Series();
        series.setTotalEpisodes(10);
        series.setWatchedEpisodes(10);
        series.setStatus(WatchStatus.WATCHING);

        mediaService.save(series);

        assertThat(series.getStatus()).isEqualTo(WatchStatus.COMPLETED);
    }

    @Test
    void save_Series_WhenWatchedExceedsTotal_ShouldCapAndSetCompleted() {
        Series series = new Series();
        series.setTotalEpisodes(10);
        series.setWatchedEpisodes(15);
        series.setStatus(WatchStatus.WATCHING);

        mediaService.save(series);

        assertThat(series.getWatchedEpisodes()).isEqualTo(10);
        assertThat(series.getStatus()).isEqualTo(WatchStatus.COMPLETED);
    }

    @Test
    void save_Series_WhenWatchedIsZeroAndStatusWatching_ShouldSetPlanned() {
        Series series = new Series();
        series.setTotalEpisodes(10);
        series.setWatchedEpisodes(0);
        series.setStatus(WatchStatus.WATCHING);

        mediaService.save(series);

        assertThat(series.getStatus()).isEqualTo(WatchStatus.PLANNED);
    }

    @Test
    void save_Series_WhenWatchedGreaterThanZeroAndStatusPlanned_ShouldSetWatching() {
        Series series = new Series();
        series.setTotalEpisodes(10);
        series.setWatchedEpisodes(1);
        series.setStatus(WatchStatus.PLANNED);

        mediaService.save(series);

        assertThat(series.getStatus()).isEqualTo(WatchStatus.WATCHING);
    }

    @Test
    void save_Series_WhenWatchedLessThanTotalAndStatusCompleted_ShouldSetWatching() {
        Series series = new Series();
        series.setTotalEpisodes(10);
        series.setWatchedEpisodes(5);
        series.setStatus(WatchStatus.COMPLETED);

        mediaService.save(series);

        assertThat(series.getStatus()).isEqualTo(WatchStatus.WATCHING);
    }

    @Test
    void updateSeriesProgress_WhenIncrementingFromZero_ShouldChangeToWatching() {
        Series series = new Series();
        series.setId(1L);
        series.setTotalEpisodes(24);
        series.setWatchedEpisodes(0);
        series.setStatus(WatchStatus.PLANNED);

        when(mediaRepository.findById(1L)).thenReturn(Optional.of(series));

        mediaService.updateSeriesProgress(1L, 1);

        assertThat(series.getWatchedEpisodes()).isEqualTo(1);
        assertThat(series.getStatus()).isEqualTo(WatchStatus.WATCHING);
    }

    @Test
    void updateSeriesProgress_WhenIncrementingToTotal_ShouldChangeToCompleted() {
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
    void updateSeriesProgress_WhenIncrementingAboveTotal_ShouldCapAtTotal() {
        Series series = new Series();
        series.setId(1L);
        series.setTotalEpisodes(5);
        series.setWatchedEpisodes(5);
        series.setStatus(WatchStatus.COMPLETED);

        when(mediaRepository.findById(1L)).thenReturn(Optional.of(series));

        mediaService.updateSeriesProgress(1L, 2);

        assertThat(series.getWatchedEpisodes()).isEqualTo(5);
        assertThat(series.getStatus()).isEqualTo(WatchStatus.COMPLETED);
    }

    @Test
    void updateSeriesProgress_WhenDecrementingBelowTotal_ShouldChangeToWatching() {
        Series series = new Series();
        series.setId(1L);
        series.setTotalEpisodes(10);
        series.setWatchedEpisodes(10);
        series.setStatus(WatchStatus.COMPLETED);

        when(mediaRepository.findById(1L)).thenReturn(Optional.of(series));

        mediaService.updateSeriesProgress(1L, -1);

        assertThat(series.getWatchedEpisodes()).isEqualTo(9);
        assertThat(series.getStatus()).isEqualTo(WatchStatus.WATCHING);
    }

    @Test
    void updateSeriesProgress_WhenDecrementingToZero_ShouldChangeToPlanned() {
        Series series = new Series();
        series.setId(1L);
        series.setTotalEpisodes(10);
        series.setWatchedEpisodes(1);
        series.setStatus(WatchStatus.WATCHING);

        when(mediaRepository.findById(1L)).thenReturn(Optional.of(series));

        mediaService.updateSeriesProgress(1L, -1);

        assertThat(series.getWatchedEpisodes()).isEqualTo(0);
        assertThat(series.getStatus()).isEqualTo(WatchStatus.PLANNED);
    }

    @Test
    void updateSeriesProgress_WhenDecrementingBelowZero_ShouldCapAtZero() {
        Series series = new Series();
        series.setId(1L);
        series.setTotalEpisodes(10);
        series.setWatchedEpisodes(0);
        series.setStatus(WatchStatus.PLANNED);

        when(mediaRepository.findById(1L)).thenReturn(Optional.of(series));

        mediaService.updateSeriesProgress(1L, -5);

        assertThat(series.getWatchedEpisodes()).isEqualTo(0);
        assertThat(series.getStatus()).isEqualTo(WatchStatus.PLANNED);
    }

    @Test
    void markAsCompleted_ForMovie_ShouldChangeStatusOnly() {
        Movie movie = new Movie();
        movie.setId(1L);
        movie.setStatus(WatchStatus.PLANNED);

        when(mediaRepository.findById(1L)).thenReturn(Optional.of(movie));

        mediaService.markAsCompleted(1L);

        assertThat(movie.getStatus()).isEqualTo(WatchStatus.COMPLETED);
    }

    @Test
    void markAsCompleted_ForSeries_ShouldChangeStatusAndMaxEpisodes() {
        Series series = new Series();
        series.setId(1L);
        series.setTotalEpisodes(12);
        series.setWatchedEpisodes(5);
        series.setStatus(WatchStatus.WATCHING);

        when(mediaRepository.findById(1L)).thenReturn(Optional.of(series));

        mediaService.markAsCompleted(1L);

        assertThat(series.getStatus()).isEqualTo(WatchStatus.COMPLETED);
        assertThat(series.getWatchedEpisodes()).isEqualTo(12);
    }

    @Test
    void markAsCompleted_ForSeriesWithNullTotal_ShouldChangeStatusOnly() {
        Series series = new Series();
        series.setId(1L);
        series.setTotalEpisodes(null);
        series.setWatchedEpisodes(5);
        series.setStatus(WatchStatus.WATCHING);

        when(mediaRepository.findById(1L)).thenReturn(Optional.of(series));

        mediaService.markAsCompleted(1L);

        assertThat(series.getStatus()).isEqualTo(WatchStatus.COMPLETED);
        assertThat(series.getWatchedEpisodes()).isEqualTo(5);
    }

    @Test
    void getStatistics_WhenEmptyDatabase_ShouldReturnZeros() {
        when(mediaRepository.findAll()).thenReturn(Collections.emptyList());

        StatisticsDto stats = mediaService.getStatistics();

        assertThat(stats.getTotalItems()).isZero();
        assertThat(stats.getMovieCount()).isZero();
        assertThat(stats.getSeriesCount()).isZero();
        assertThat(stats.getTotalDurationMinutes()).isZero();
        assertThat(stats.getWatchedDurationMinutes()).isZero();
    }

    @Test
    void getStatistics_ShouldCalculateCorrectly() {
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