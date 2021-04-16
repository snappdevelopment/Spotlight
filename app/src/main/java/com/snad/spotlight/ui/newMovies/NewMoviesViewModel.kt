package com.snad.spotlight.ui.newMovies

import androidx.lifecycle.*
import com.snad.spotlight.repository.NewMoviesRepository
import com.snad.spotlight.repository.NewMoviesResult
import com.snad.spotlight.network.models.NewMovies
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NewMoviesViewModel(
    private val newMoviesRepository: NewMoviesRepository,
    private val disposables: CompositeDisposable
) : ViewModel() {

    private val _state = MutableLiveData<NewMoviesState>(NewMoviesState.LoadingState)
    val state: LiveData<NewMoviesState>
        get() = _state

    init {
        loadNewMovies()
    }

    fun loadNewMovies() {
        disposables.add(
            newMoviesRepository.loadNewMovies()
            .toObservable()
            .map { it.toNewMoviesState() }
            .observeOn(AndroidSchedulers.mainThread())
            .distinctUntilChanged()
            .subscribe { _state.value = it }
        )

//        state.value = NewMoviesState.LoadingState
//        viewModelScope.launch(Dispatchers.IO) {
//            val result = newMoviesRepository.loadNewMovies()
//            withContext(Dispatchers.Main) {
//                when(result) {
//                    is NewMoviesResult.Success -> {
//                        val sortedMovies = result.newMovies.movies.sortedByDescending { listMovie -> listMovie.popularity }
//                        state.value = NewMoviesState.DoneState(result.newMovies.copy(movies = sortedMovies))
//                    }
//                    is NewMoviesResult.NetworkError -> state.value = NewMoviesState.NetworkErrorState
//                    is NewMoviesResult.ConnectionError -> state.value = NewMoviesState.NetworkErrorState
//                    is NewMoviesResult.AuthenticationError -> state.value = NewMoviesState.AuthenticationErrorState
//                    is NewMoviesResult.ApiError -> state.value = NewMoviesState.ErrorState
//                    is NewMoviesResult.Error -> state.value = NewMoviesState.ErrorState
//                }
//            }
//        }
    }

    private fun NewMoviesResult.toNewMoviesState(): NewMoviesState {
        return when(this) {
            is NewMoviesResult.Success -> {
                val sortedMovies = this.newMovies.movies.sortedByDescending { listMovie -> listMovie.popularity }
                NewMoviesState.DoneState(this.newMovies.copy(movies = sortedMovies))
            }
            is NewMoviesResult.NetworkError -> NewMoviesState.NetworkErrorState
            is NewMoviesResult.ConnectionError -> NewMoviesState.NetworkErrorState
            is NewMoviesResult.AuthenticationError -> NewMoviesState.AuthenticationErrorState
            is NewMoviesResult.ApiError -> NewMoviesState.ErrorState
            is NewMoviesResult.Error -> NewMoviesState.ErrorState
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }

    class Factory @Inject constructor(
        private val newMoviesRepository: NewMoviesRepository,
        private val disposables: CompositeDisposable
    ): ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return NewMoviesViewModel(newMoviesRepository, disposables) as T
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