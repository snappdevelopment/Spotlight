package com.snad.sniffer.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModelProvider
import com.snad.sniffer.logging.NetworkDataRepository
import com.snad.sniffer.logging.NetworkRequest
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

internal class DetailsViewModel(
    private val repository: NetworkDataRepository
): ViewModel() {

    fun state(requestId: Long): StateFlow<DetailsState> = repository.request(requestId)
        .map { it.toDetailsState() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), DetailsState.Initial)

    private fun NetworkRequest?.toDetailsState(): DetailsState {
        return if(this == null) {
            DetailsState.Error
        } else {
            DetailsState.Content(
                networkRequestDetailsItem = this.toNetworkRequestDetailsItem()
            )
        }
    }

    private fun NetworkRequest.toNetworkRequestDetailsItem(): NetworkRequestDetailsItem {
        return NetworkRequestDetailsItem(url)
    }

    class Factory(
        private val repository: NetworkDataRepository,
    ): ViewModelProvider.Factory {
        @Suppress("Unchecked_cast")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return DetailsViewModel(repository) as T
        }
    }
}