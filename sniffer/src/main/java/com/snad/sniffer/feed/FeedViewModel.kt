package com.snad.sniffer.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.snad.sniffer.logging.NetworkDataRepository
import com.snad.sniffer.logging.NetworkRequest
import com.snad.sniffer.util.DateTimeFormatter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class FeedViewModel(
    private val repository: NetworkDataRepository,
    private val dateTimeFormatter: DateTimeFormatter
): ViewModel() {

    val state: Flow<List<FeedUiModel>> = repository.requests.map { it.toFeedUiModel() }

    private fun List<NetworkRequest>.toFeedUiModel(): List<FeedUiModel> {
        return map {
            FeedUiModel(
                dateTime = dateTimeFormatter.format(it.timestampMillis),
                method = it.method,
                statusCode = it.statusCode,
                duration = "${it.durationMillis}ms",
                url = it.url
            )
        }
    }

    class Factory(
        private val repository: NetworkDataRepository,
        private val dateTimeFormatter: DateTimeFormatter
    ): ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return FeedViewModel(repository, dateTimeFormatter) as T
        }
    }
}