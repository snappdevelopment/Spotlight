package com.snad.sniffer.feed

import androidx.lifecycle.*
import com.snad.sniffer.logging.NetworkDataRepository
import com.snad.sniffer.logging.NetworkRequest
import com.snad.sniffer.util.DateTimeFormatter
import kotlinx.coroutines.flow.*

internal class FeedViewModel(
    private val repository: NetworkDataRepository,
    private val dateTimeFormatter: DateTimeFormatter
): ViewModel() {

    val state: StateFlow<List<NetworkRequestListItem>> = repository.requests
        .map { it.toNetworkRequestListItem() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private fun List<NetworkRequest>.toNetworkRequestListItem(): List<NetworkRequestListItem> {
        return map {
            when(it) {
                is NetworkRequest.Ongoing -> NetworkRequestListItem.Ongoing(
                    id = it.id,
                    dateTime = dateTimeFormatter.format(it.timestampMillis),
                    url = it.url,
                    method = it.method
                )
                is NetworkRequest.Finished -> NetworkRequestListItem.Finished(
                    id = it.id,
                    dateTime = dateTimeFormatter.format(it.timestampMillis),
                    url = it.url,
                    method = it.method,
                    duration = "${it.durationMillis}ms",
                    statusCode = it.statusCode
                )
                is NetworkRequest.Failed -> NetworkRequestListItem.Failed(
                    id = it.id,
                    dateTime = dateTimeFormatter.format(it.timestampMillis),
                    url = it.url,
                    method = it.method,
                    errorMessage = it.errorMessage
                )
            }
        }
    }

    class Factory(
        private val repository: NetworkDataRepository,
        private val dateTimeFormatter: DateTimeFormatter
    ): ViewModelProvider.Factory {
        @Suppress("Unchecked_cast")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return FeedViewModel(repository, dateTimeFormatter) as T
        }
    }
}