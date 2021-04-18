package com.snad.spotlight.ui.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.snad.spotlight.repository.SearchRepository
import com.snad.spotlight.repository.SearchRepositoryResult
import com.snad.spotlight.network.models.ListMovie
import com.snad.spotlight.repository.LibraryRepository
import com.snad.spotlight.ui.library.LibraryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SearchViewModel(
    private val searchRepository: SearchRepository
) : ViewModel() {

    val state: MutableLiveData<SearchState> = MutableLiveData(SearchState.InitialState)

    fun searchMovies(title: String) {
        state.value = SearchState.LoadingState
        viewModelScope.launch(Dispatchers.IO) {
            val result = searchRepository.searchMovies(title)
            withContext(Dispatchers.Main) {
                when(result) {
                    is SearchRepositoryResult.Success -> {
                        if(result.searchResults.total_results == 0) state.value = SearchState.NoResultsState
                        else {
                            val sortedList = result.searchResults.results.sortedByDescending { listMovie ->
                                listMovie.popularity
                            }
                            state.value = SearchState.DoneState(sortedList)
                        }
                    }
                    is SearchRepositoryResult.NetworkError -> state.value = SearchState.NetworkErrorState
                    is SearchRepositoryResult.ConnectionError -> state.value = SearchState.NetworkErrorState
                    is SearchRepositoryResult.AuthenticationError -> state.value = SearchState.AuthenticationErrorState
                    is SearchRepositoryResult.ApiError -> state.value = SearchState.ErrorState
                    is SearchRepositoryResult.Error -> state.value = SearchState.ErrorState
                }
            }
        }
    }

    class Factory @Inject constructor(
        private  val searchRepository: SearchRepository
    ): ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SearchViewModel(searchRepository) as T
        }
    }
}

sealed class SearchState {
    class DoneState(val searchResults: List<ListMovie>): SearchState()
    object InitialState: SearchState()
    object LoadingState: SearchState()
    object NoResultsState: SearchState()
    object NetworkErrorState: SearchState()
    object AuthenticationErrorState: SearchState()
    object ErrorState: SearchState()
}