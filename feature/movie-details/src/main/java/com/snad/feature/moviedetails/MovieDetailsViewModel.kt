package com.snad.feature.moviedetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.snad.feature.moviedetails.repository.MovieDetailsRepository
import com.snad.feature.moviedetails.repository.MovieDetailsResult
import com.snad.core.persistence.models.LibraryMovie
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject

internal class MovieDetailsViewModel(
    private val movieDetailsRepository: MovieDetailsRepository,
    private val ioDispatcher: CoroutineDispatcher,
    private val clock: Clock
): ViewModel() {

    private val _state = MutableStateFlow<MovieDetailsState>(MovieDetailsState.LoadingState)
    val state: StateFlow<MovieDetailsState> = _state

    fun loadMovie(id: Int) {
        viewModelScope.launch(ioDispatcher) {
            movieDetailsRepository.loadMovie(id).collect { movieDetailsResult ->
                updateState(movieDetailsResult.toMovieDetailsState())
                updateOutdatedMovie(movieDetailsResult)
            }
        }
    }

    fun addOrRemoveMovie() {
        val currentState = state.value
        if((currentState as MovieDetailsState.DoneState).isInLibrary) {
            viewModelScope.launch(ioDispatcher) {
                movieDetailsRepository.deleteMovie(currentState.movie)
            }
        } else {
            viewModelScope.launch(ioDispatcher) {
                val libraryMovie = currentState.movie.copy(
                    added_at = LocalDate.now(clock),
                    updated_at = LocalDate.now(clock)
                )
                movieDetailsRepository.addMovie(libraryMovie)
            }
        }
    }

    fun toggleHasBeenWatched() {
        val currentState = state.value
        if(currentState is MovieDetailsState.DoneState) {
            viewModelScope.launch(ioDispatcher) {
                val updatedMovie = currentState.movie.copy(
                    has_been_watched = !currentState.movie.has_been_watched
                )
                movieDetailsRepository.updateMovie(updatedMovie)
            }
        }
    }

    private suspend fun updateOutdatedMovie(movieDetailsResult: MovieDetailsResult) {
        if((movieDetailsResult as? MovieDetailsResult.Success)?.isInLibrary == true) {
            val movieIsOutdated = movieDetailsResult.movie.updated_at
                ?.plusDays(2)
                ?.isBefore( LocalDate.now(clock))

            if(movieIsOutdated == true) movieDetailsRepository.updateMovieData(movieDetailsResult.movie)
        }
    }

    private fun MovieDetailsResult.toMovieDetailsState(): MovieDetailsState {
        return when(this) {
            is MovieDetailsResult.Success -> {
                val sortedBackdrops = movie.backdrops
                    .sortedByDescending { backdrop -> backdrop.vote_average }
                    .filter { backdrop -> backdrop.file_path != movie.backdrop_path }
                val sortedCast = movie.cast.sortedBy { castMember -> castMember.order }

                MovieDetailsState.DoneState(
                    movie = movie.copy(
                        backdrops = sortedBackdrops,
                        cast = sortedCast
                    ),
                    isInLibrary = isInLibrary
                )
            }
            is MovieDetailsResult.NetworkError -> MovieDetailsState.ErrorNetworkState
            is MovieDetailsResult.ConnectionError -> MovieDetailsState.ErrorNetworkState
            is MovieDetailsResult.AuthenticationError -> MovieDetailsState.ErrorAuthenticationState
            is MovieDetailsResult.ApiError -> MovieDetailsState.ErrorState
            is MovieDetailsResult.Error -> MovieDetailsState.ErrorState
        }
    }

    private fun updateState(newState: MovieDetailsState) {
        _state.value = newState
    }

    class Factory @Inject constructor(
        private  val movieDetailsRepository: MovieDetailsRepository,
        private val ioDispatcher: CoroutineDispatcher,
        private val clock: Clock
    ): ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MovieDetailsViewModel(movieDetailsRepository, ioDispatcher, clock) as T
        }
    }
}

internal sealed class MovieDetailsState {
    data class DoneState(val movie: LibraryMovie, val isInLibrary: Boolean): MovieDetailsState()
    object LoadingState: MovieDetailsState()
    object ErrorNetworkState: MovieDetailsState()
    object ErrorAuthenticationState: MovieDetailsState()
    object ErrorState: MovieDetailsState()
}