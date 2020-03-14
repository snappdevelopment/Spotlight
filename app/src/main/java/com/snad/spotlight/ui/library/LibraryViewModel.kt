package com.snad.spotlight.ui.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snad.spotlight.LibraryRepository
import com.snad.spotlight.LibraryRepositoryResult
import com.snad.spotlight.network.models.Movie
import com.snad.spotlight.persistence.models.LibraryMovie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LibraryViewModel(
    private val libraryRepository: LibraryRepository
) : ViewModel() {

    val state = MutableLiveData<LibraryState>()

    fun loadLibraryMovies() {
        state.value = LibraryState.LoadingState
        viewModelScope.launch(Dispatchers.IO) {
            libraryRepository.loadLibraryMovies().collect {
                withContext(Dispatchers.Main) {
                    when(it) {
                        is LibraryRepositoryResult.Success -> state.value =LibraryState.DoneState(it.libraryMovies)
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

}

sealed class LibraryState {
    class DoneState(val libraryMovies: List<LibraryMovie>): LibraryState()
    object LoadingState: LibraryState()
    object ErrorState: LibraryState()
}