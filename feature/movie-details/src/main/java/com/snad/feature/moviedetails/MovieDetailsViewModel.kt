package com.snad.feature.moviedetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.snad.feature.moviedetails.repository.MovieDetailsRepository
import com.snad.feature.moviedetails.repository.MovieDetailsResult
import com.snad.core.persistence.models.LibraryMovie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

internal class MovieDetailsViewModel(
    private val movieDetailsRepository: MovieDetailsRepository
): ViewModel() {

    val state: MutableLiveData<MovieDetailsState> = MutableLiveData()

    fun loadMovie(id: Int) {
        updateState(MovieDetailsState.LoadingState)

        viewModelScope.launch(Dispatchers.IO) {
            movieDetailsRepository.loadMovie(id).collect { movieDetailsResult ->
                withContext(Dispatchers.Main) {
                    updateState(movieDetailsResult.toMovieDetailsState())
                }
                if((movieDetailsResult as? MovieDetailsResult.Success)?.isInLibrary == true) {
                    movieDetailsRepository.updateMovieData(movieDetailsResult.movie)
                }
            }
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
        state.value = newState
    }

    fun addOrRemoveMovie() {
        val currentState = state.value
        if((currentState as MovieDetailsState.DoneState).isInLibrary) {
            viewModelScope.launch(Dispatchers.IO) {
                movieDetailsRepository.deleteMovie(currentState.movie)
            }
        }
        else {
            viewModelScope.launch(Dispatchers.IO) {
                val libraryMovie = currentState.movie.copy(
                    added_at = Calendar.getInstance(),
                    updated_at = Calendar.getInstance()
                )
                movieDetailsRepository.addMovie(libraryMovie)
            }
        }
    }

    fun toggleHasBeenWatched() {
        val currentState = state.value
        if(currentState is MovieDetailsState.DoneState) {
            viewModelScope.launch(Dispatchers.IO) {
                val updatedMovie = currentState.movie.copy(
                    has_been_watched = !currentState.movie.has_been_watched
                )
                movieDetailsRepository.updateMovie(updatedMovie)
            }
        }
    }

    class Factory @Inject constructor(
        private  val movieDetailsRepository: MovieDetailsRepository
    ): ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MovieDetailsViewModel(movieDetailsRepository) as T
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