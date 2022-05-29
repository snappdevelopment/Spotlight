package com.snad.sniffer.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.snad.sniffer.logging.NetworkDataRepository

internal class FeedViewModel(
    private val repository: NetworkDataRepository
): ViewModel() {

    val state = repository.requests

    class Factory(
        private val repository: NetworkDataRepository
    ): ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return FeedViewModel(repository) as T
        }
    }
}