package com.tracker.mediatracker.service;

import com.tracker.mediatracker.dto.MovieFormDto;
import com.tracker.mediatracker.dto.SeriesFormDto;
import com.tracker.mediatracker.dto.StatisticsDto;
import com.tracker.mediatracker.model.Movie;
import com.tracker.mediatracker.model.Series;
import com.tracker.mediatracker.model.WatchStatus;
import com.tracker.mediatracker.repo.MediaItemRepository;
import com.tracker.mediatracker.repo.StatsProjection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    void saveMovie_ShouldUpdateEntityAndSave() {
        MovieFormDto dto = new MovieFormDto();
        dto.setTitle("Test Movie");
        dto.setStatus(WatchStatus.PLANNED);

        mediaService.saveMovie(dto);

        ArgumentCaptor<Movie> captor = ArgumentCaptor.forClass(Movie.class);
        verify(mediaRepository).save(captor.capture());

        Movie savedMovie = captor.getValue();
        assertThat(savedMovie.getTitle()).isEqualTo("Test Movie");
        assertThat(savedMovie.getStatus()).isEqualTo(WatchStatus.PLANNED);
    }

    @Test
    void saveSeries_ShouldUpdateEntitySyncStateAndSave() {
        SeriesFormDto dto = new SeriesFormDto();
        dto.setTitle("Test Series");
        dto.setTotalEpisodes(10);
        dto.setWatchedEpisodes(10);
        dto.setStatus(WatchStatus.WATCHING);

        mediaService.saveSeries(dto);

        ArgumentCaptor<Series> captor = ArgumentCaptor.forClass(Series.class);
        verify(mediaRepository).save(captor.capture());

        Series savedSeries = captor.getValue();
        assertThat(savedSeries.getTitle()).isEqualTo("Test Series");
        assertThat(savedSeries.getStatus()).isEqualTo(WatchStatus.COMPLETED);
    }

    @Test
    void updateSeriesProgress_WhenIncrementing_ShouldSaveUpdatedSeries() {
        Series series = new Series();
        series.setId(1L);
        series.setTotalEpisodes(10);
        series.setWatchedEpisodes(5);
        series.setStatus(WatchStatus.WATCHING);

        when(mediaRepository.findById(1L)).thenReturn(Optional.of(series));

        mediaService.updateSeriesProgress(1L, 1);

        verify(mediaRepository).save(series);
        assertThat(series.getWatchedEpisodes()).isEqualTo(6);
    }

    @Test
    void markAsCompleted_ForMovie_ShouldSetCompleted() {
        Movie movie = new Movie();
        movie.setId(1L);
        movie.setStatus(WatchStatus.PLANNED);

        when(mediaRepository.findById(1L)).thenReturn(Optional.of(movie));

        mediaService.markAsCompleted(1L);

        verify(mediaRepository).save(movie);
        assertThat(movie.getStatus()).isEqualTo(WatchStatus.COMPLETED);
    }

    @Test
    void markAsCompleted_ForSeries_ShouldSetCompletedAndMaxEpisodes() {
        Series series = new Series();
        series.setId(1L);
        series.setTotalEpisodes(12);
        series.setWatchedEpisodes(5);
        series.setStatus(WatchStatus.WATCHING);

        when(mediaRepository.findById(1L)).thenReturn(Optional.of(series));

        mediaService.markAsCompleted(1L);

        verify(mediaRepository).save(series);
        assertThat(series.getStatus()).isEqualTo(WatchStatus.COMPLETED);
        assertThat(series.getWatchedEpisodes()).isEqualTo(12);
    }

    @Test
    void getStatistics_ShouldUseProjection() {
        StatsProjection proj = mock(StatsProjection.class);
        when(proj.getTotalItems()).thenReturn(10L);
        when(proj.getMovieCount()).thenReturn(6L);
        when(proj.getSeriesCount()).thenReturn(4L);
        when(proj.getCompletedCount()).thenReturn(3L);
        when(proj.getTotalDuration()).thenReturn(1500L);

        when(mediaRepository.getStats()).thenReturn(proj);

        StatisticsDto stats = mediaService.getStatistics();

        assertThat(stats.getTotalItems()).isEqualTo(10L);
        assertThat(stats.getMovieCount()).isEqualTo(6L);
        assertThat(stats.getSeriesCount()).isEqualTo(4L);
        assertThat(stats.getCompletedCount()).isEqualTo(3L);
        assertThat(stats.getTotalDurationMinutes()).isEqualTo(1500L);
    }

    @Test
    void getStatistics_WhenNullProjection_ShouldReturnEmptyStats() {
        when(mediaRepository.getStats()).thenReturn(null);

        StatisticsDto stats = mediaService.getStatistics();

        assertThat(stats.getTotalItems()).isZero();
        assertThat(stats.getMovieCount()).isZero();
    }
}