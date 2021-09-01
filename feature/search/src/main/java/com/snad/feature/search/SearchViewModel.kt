package com.snad.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.snad.feature.search.model.ListMovie
import com.snad.feature.search.repository.SearchRepository
import com.snad.feature.search.repository.SearchRepositoryResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class SearchViewModel(
    private val searchRepository: SearchRepository,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _state = MutableStateFlow<SearchState>(SearchState.InitialState)
    val state = _state.asStateFlow()

    fun searchMovies(title: String) {
        updateState(SearchState.LoadingState)
        viewModelScope.launch(ioDispatcher) {
            val result = searchRepository.searchMovies(title)
            when(result) {
                is SearchRepositoryResult.Success -> {
                    if(result.searchResults.total_results == 0) updateState(SearchState.NoResultsState)
                    else {
                        val sortedList = result.searchResults.results.sortedByDescending { listMovie ->
                            listMovie.popularity
                        }
                        updateState(SearchState.DoneState(sortedList))
                    }
                }
                is SearchRepositoryResult.NetworkError -> updateState(SearchState.NetworkErrorState)
                is SearchRepositoryResult.ConnectionError -> updateState(SearchState.NetworkErrorState)
                is SearchRepositoryResult.AuthenticationError -> updateState(SearchState.AuthenticationErrorState)
                is SearchRepositoryResult.ApiError -> updateState(SearchState.ErrorState)
                is SearchRepositoryResult.Error -> updateState(SearchState.ErrorState)
            }
        }
    }

    private fun updateState(state: SearchState) {
        _state.value = state
    }

    class Factory @Inject constructor(
        private  val searchRepository: SearchRepository,
        private val ioDispatcher: CoroutineDispatcher
    ): ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SearchViewModel(searchRepository, ioDispatcher) as T
        }
    }
}

internal sealed class SearchState {
    data class DoneState(val searchResults: List<ListMovie>): SearchState()
    object InitialState: SearchState()
    object LoadingState: SearchState()
    object NoResultsState: SearchState()
    object NetworkErrorState: SearchState()
    object AuthenticationErrorState: SearchState()
    object ErrorState: SearchState()
}