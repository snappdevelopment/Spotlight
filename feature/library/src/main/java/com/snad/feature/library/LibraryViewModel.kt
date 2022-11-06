package com.snad.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.snad.core.arch.StateMachine
import com.snad.core.persistence.models.LibraryMovie
import com.snad.feature.library.repository.LibraryRepository
import com.snad.feature.library.repository.LibraryRepositoryResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class LibraryViewModel(
    private val libraryRepository: LibraryRepository,
    private val ioDispatcher: CoroutineDispatcher
) : StateMachine<LibraryState, LibraryAction>(LibraryState.LoadingState) {

    override fun handleAction(action: LibraryAction) {
        when(action) {
            is LoadMovies -> loadLibraryMovies()
            is DeleteMovie -> deleteLibraryMovie(action.libraryMovie)
            is UpdateMovie -> updateLibraryMovie(action.libraryMovie)
        }
    }

    private fun loadLibraryMovies() {
        viewModelScope.launch(ioDispatcher) {
            updateState(LibraryState.LoadingState)

            libraryRepository.loadLibraryMovies().collect {
                when(it) {
                    is LibraryRepositoryResult.Success -> {
                        if(it.libraryMovies.isEmpty()) {
                            updateState(LibraryState.EmptyState)
                        } else {
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

    private fun updateLibraryMovie(libraryMovie: LibraryMovie) {
        viewModelScope.launch(ioDispatcher) {
            libraryRepository.updateMovie(libraryMovie)
        }
    }

    private fun deleteLibraryMovie(libraryMovie: LibraryMovie) {
        viewModelScope.launch(ioDispatcher) {
            libraryRepository.deleteMovie(libraryMovie)
        }
    }

    class Factory @Inject constructor(
        private  val libraryRepository: LibraryRepository,
        private val ioDispatcher: CoroutineDispatcher
    ): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
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