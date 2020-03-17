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
                    is MovieDetailsResult.ErrorUnknown -> state.value = MovieDetailsState.ErrorState
                }
            }
        }
    }

    fun addMovieToLibrary() {
        val state = state.value
        if(state is MovieDetailsState.DoneState) {
            viewModelScope.launch(Dispatchers.IO) {
                movieDetailsRepository.addMovie(state.movie)
            }
        }
    }
}

sealed class MovieDetailsState {
    data class DoneState(val movie: LibraryMovie, val isInLibrary: Boolean): MovieDetailsState()
    object LoadingState: MovieDetailsState()
    object ErrorState: MovieDetailsState()
}