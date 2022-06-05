package com.snad.sniffer.logging

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal interface NetworkDataRepository {
    val requests: StateFlow<List<NetworkRequest>>
    fun add(data: NetworkRequest)
    fun clear()

    companion object {
        fun get(): NetworkDataRepository {
            return InMemoryNetworkDataRepository()
        }
    }
}

private class InMemoryNetworkDataRepository : NetworkDataRepository {

    private val networkRequests = mutableListOf<NetworkRequest>()
    private val updates = MutableStateFlow(networkRequests)

    override val requests = updates.asStateFlow()

    override fun add(data: NetworkRequest) {
        networkRequests.add(data)
        updates.tryEmit(networkRequests)
    }

    override fun clear() {
        networkRequests.clear()
        updates.tryEmit(networkRequests)
    }
}
