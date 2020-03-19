package com.snad.spotlight.ui.movieDetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snad.spotlight.MovieDetailsRepository
import com.snad.spotlight.MovieDetailsResult
import com.snad.spotlight.persistence.models.LibraryMovie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MovieDetailsViewModel(
    private val movieDetailsRepository: MovieDetailsRepository
): ViewModel() {

    val state: MutableLiveData<MovieDetailsState> = MutableLiveData()

    fun loadMovie(id: Int) {
        state.value = MovieDetailsState.LoadingState
        viewModelScope.launch(Dispatchers.IO) {
            val result = movieDetailsRepository.loadMovie(id)
            withContext(Dispatchers.Main) {
                when(result) {
                    is MovieDetailsResult.Success -> state.value = MovieDetailsState.DoneState(result.movie, result.isInLibrary)
                    is MovieDetailsResult.NetworkError -> state.value = MovieDetailsState.ErrorNetworkState
                    is MovieDetailsResult.ConnectionError -> state.value = MovieDetailsState.ErrorNetworkState
                    is MovieDetailsResult.AuthenticationError -> state.value = MovieDetailsState.ErrorAuthenticationState
                    is MovieDetailsResult.ApiError -> state.value = MovieDetailsState.ErrorState
                    is MovieDetailsResult.Error -> state.value = MovieDetailsState.ErrorState
                }
            }
        }
    }

    fun addOrRemoveMovie() {
        val currentState = state.value
        if(currentState is MovieDetailsState.DoneState) {
            if(currentState.isInLibrary) {
                viewModelScope.launch(Dispatchers.IO) {
                    movieDetailsRepository.deleteMovie(currentState.movie)
                    withContext(Dispatchers.Main) {
                        state.value = MovieDetailsState.DoneState(currentState.movie, false)
                    }
                }
            }
            else {
                viewModelScope.launch(Dispatchers.IO) {
                    val libraryMovie = currentState.movie.copy(added_at = Calendar.getInstance())
                    movieDetailsRepository.addMovie(libraryMovie)
                    withContext(Dispatchers.Main) {
                        state.value = MovieDetailsState.DoneState(libraryMovie, true)
                    }
                }
            }
        }
    }

    fun toogleHasBeenWatched() {
        val currentState = state.value
        if(currentState is MovieDetailsState.DoneState) {
            viewModelScope.launch(Dispatchers.IO) {
                val updatedMovie = currentState.movie.copy(has_been_watched = !currentState.movie.has_been_watched)
                movieDetailsRepository.updateMovie(updatedMovie)
                withContext(Dispatchers.Main) {
                    state.value = MovieDetailsState.DoneState(updatedMovie, true)
                }
            }
        }
    }
}

sealed class MovieDetailsState {
    data class DoneState(val movie: LibraryMovie, val isInLibrary: Boolean): MovieDetailsState()
    object LoadingState: MovieDetailsState()
    object ErrorNetworkState: MovieDetailsState()
    object ErrorAuthenticationState: MovieDetailsState()
    object ErrorState: MovieDetailsState()
}