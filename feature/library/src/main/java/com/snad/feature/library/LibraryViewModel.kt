package com.snad.feature.library

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.snad.core.persistence.models.LibraryMovie
import com.snad.feature.library.repository.LibraryRepository
import com.snad.feature.library.repository.LibraryRepositoryResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class LibraryViewModel(
    private val libraryRepository: LibraryRepository,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _state = MutableStateFlow<LibraryState>(LibraryState.LoadingState)
    val state: StateFlow<LibraryState> = _state

    fun loadLibraryMovies() {
        updateState(LibraryState.LoadingState)

        viewModelScope.launch(ioDispatcher) {
            libraryRepository.loadLibraryMovies().collect {
                when(it) {
                    is LibraryRepositoryResult.Success -> {
                        if(it.libraryMovies.isEmpty()) {
                            updateState(LibraryState.EmptyState)
                        }
                        else {
                            val sortedLibraryMovies = it.libraryMovies
                                .sortedByDescending { libraryMovie -> libraryMovie.added_at }
                            updateState(LibraryState.DoneState(sortedLibraryMovies))
                        }
                    }
                    is LibraryRepositoryResult.DbError -> updateState(LibraryState.ErrorState)
                }
            }
        }
    }

    fun updateLibraryMovie(libraryMovie: LibraryMovie) {
        viewModelScope.launch(ioDispatcher) {
            libraryRepository.updateMovie(libraryMovie)
        }
    }

    fun deleteLibraryMovie(libraryMovie: LibraryMovie) {
        viewModelScope.launch(ioDispatcher) {
            libraryRepository.deleteMovie(libraryMovie)
        }
    }

    private fun updateState(newState: LibraryState) {
        _state.value = newState
    }

    class Factory @Inject constructor(
        private  val libraryRepository: LibraryRepository,
        private val ioDispatcher: CoroutineDispatcher
    ): ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return LibraryViewModel(libraryRepository, ioDispatcher) as T
        }
    }
}

internal sealed class LibraryState {
    data class DoneState(val libraryMovies: List<LibraryMovie>): LibraryState()
    object EmptyState: LibraryState()
    object LoadingState: LibraryState()
    object ErrorState: LibraryState()
}