package com.snad.spotlight.ui.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snad.spotlight.network.models.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val libraryRepository: LibraryRepository
) : ViewModel() {

    val state = MutableLiveData<LibraryState>()

    fun loadLibraryMovies() {
        state.value =
        viewModelScope.launch(Dispatchers.IO) {
            val result = libraryRepository.loadLibraryMovies()
            when(result) {

            }
        }
    }

}

sealed class LibraryState {
    class DoneState(val libraryMovies: Movie): LibraryState()
    object LoadingState: LibraryState()
    object DbErrorState: LibraryState()
    object ErrorState: LibraryState()
}