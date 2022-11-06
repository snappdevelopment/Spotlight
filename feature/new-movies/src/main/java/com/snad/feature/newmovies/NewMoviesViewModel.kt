package com.snad.feature.newmovies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.snad.core.arch.StateMachine
import com.snad.feature.newmovies.repository.NewMoviesRepositoryImpl
import com.snad.feature.newmovies.repository.NewMoviesResult
import com.snad.feature.newmovies.model.NewMovies
import com.snad.feature.newmovies.repository.NewMoviesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class NewMoviesViewModel(
    private val newMoviesRepository: NewMoviesRepository,
    private val ioDispatcher: CoroutineDispatcher
) : StateMachine<NewMoviesState, NewMoviesAction>(NewMoviesState.LoadingState) {

    override fun handleAction(action: NewMoviesAction) {
        when(action) {
            is LoadMovies -> loadNewMovies()
        }
    }

    private fun loadNewMovies() {
        viewModelScope.launch(ioDispatcher) {
            updateState(NewMoviesState.LoadingState)
            val result = newMoviesRepository.loadNewMovies()
            when(result) {
                is NewMoviesResult.Success -> {
                    val sortedMovies = result.newMovies.movies.sortedByDescending { listMovie -> listMovie.popularity }
                    updateState(NewMoviesState.DoneState(result.newMovies.copy(movies = sortedMovies)))
                }
                is NewMoviesResult.NetworkError -> updateState(NewMoviesState.NetworkErrorState)
                is NewMoviesResult.ConnectionError -> updateState(NewMoviesState.NetworkErrorState)
                is NewMoviesResult.AuthenticationError -> updateState(NewMoviesState.AuthenticationErrorState)
                is NewMoviesResult.ApiError -> updateState(NewMoviesState.ErrorState)
                is NewMoviesResult.Error -> updateState(NewMoviesState.ErrorState)
            }
        }
    }

    class Factory @Inject constructor(
        private  val newMoviesRepository: NewMoviesRepositoryImpl,
        private val ioDispatcher: CoroutineDispatcher
    ): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NewMoviesViewModel(newMoviesRepository, ioDispatcher) as T
        }
    }
}

internal sealed class NewMoviesState {
    data class DoneState(val newMovies: NewMovies): NewMoviesState()
    object LoadingState: NewMoviesState()
    object NetworkErrorState: NewMoviesState()
    object AuthenticationErrorState: NewMoviesState()
    object ErrorState: NewMoviesState()
}