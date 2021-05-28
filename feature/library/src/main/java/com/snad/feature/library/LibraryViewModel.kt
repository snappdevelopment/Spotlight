package com.snad.feature.library

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.snad.core.persistence.models.LibraryMovie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class LibraryViewModel(
    private val libraryRepository: LibraryRepository
) : ViewModel() {

    val state = MutableLiveData<LibraryState>()

    fun loadLibraryMovies() {
        state.value = LibraryState.LoadingState
        viewModelScope.launch(Dispatchers.IO) {
            libraryRepository.loadLibraryMovies().collect {
                withContext(Dispatchers.Main) {
                    when(it) {
                        is LibraryRepositoryResult.Success -> {
                            if(it.libraryMovies.isEmpty()) {
                                state.value = LibraryState.EmptyState
                            }
                            else {
                                val sortedLibraryMovies = it.libraryMovies.sortedByDescending { libraryMovie -> libraryMovie.added_at }
                                state.value = LibraryState.DoneState(sortedLibraryMovies)
                            }
                        }
                        is LibraryRepositoryResult.DbError -> state.value = LibraryState.ErrorState
                    }
                }
            }
        }
    }

    fun updateLibraryMovie(libraryMovie: LibraryMovie) {
        viewModelScope.launch(Dispatchers.IO) {
            libraryRepository.updateMovie(libraryMovie)
        }
    }

    fun deleteLibraryMovie(libraryMovie: LibraryMovie) {
        viewModelScope.launch(Dispatchers.IO) {
            libraryRepository.deleteMovie(libraryMovie)
        }
    }

    class Factory @Inject constructor(
        private  val libraryRepository: LibraryRepository
    ): ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return LibraryViewModel(libraryRepository) as T
        }
    }
}

sealed class LibraryState {
    class DoneState(val libraryMovies: List<LibraryMovie>): LibraryState()
    object EmptyState: LibraryState()
    object LoadingState: LibraryState()
    object ErrorState: LibraryState()
}