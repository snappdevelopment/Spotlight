package com.snad.spotlight.ui.newMovies

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.snad.spotlight.repository.NewMoviesRepository
import com.snad.spotlight.repository.NewMoviesResult
import com.snad.spotlight.network.models.NewMovies
import com.snad.spotlight.repository.LibraryRepository
import com.snad.spotlight.ui.library.LibraryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NewMoviesViewModel(
    private val newMoviesRepository: NewMoviesRepository
) : ViewModel() {

    val state = MutableLiveData<NewMoviesState>()

    fun loadNewMovies() {
        state.value = NewMoviesState.LoadingState
        viewModelScope.launch(Dispatchers.IO) {
            val result = newMoviesRepository.loadNewMovies()
            withContext(Dispatchers.Main) {
                when(result) {
                    is NewMoviesResult.Success -> {
                        val sortedMovies = result.newMovies.movies.sortedByDescending { listMovie -> listMovie.popularity }
                        state.value = NewMoviesState.DoneState(result.newMovies.copy(movies = sortedMovies))
                    }
                    is NewMoviesResult.NetworkError -> state.value = NewMoviesState.NetworkErrorState
                    is NewMoviesResult.ConnectionError -> state.value = NewMoviesState.NetworkErrorState
                    is NewMoviesResult.AuthenticationError -> state.value = NewMoviesState.AuthenticationErrorState
                    is NewMoviesResult.ApiError -> state.value = NewMoviesState.ErrorState
                    is NewMoviesResult.Error -> state.value = NewMoviesState.ErrorState
                }
            }
        }
    }

    class Factory @Inject constructor(
        private  val newMoviesRepository: NewMoviesRepository
    ): ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return NewMoviesViewModel(newMoviesRepository) as T
        }
    }
}

sealed class NewMoviesState {
    class DoneState(val newMovies: NewMovies): NewMoviesState()
    object LoadingState: NewMoviesState()
    object NetworkErrorState: NewMoviesState()
    object AuthenticationErrorState: NewMoviesState()
    object ErrorState: NewMoviesState()
}