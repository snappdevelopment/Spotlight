package com.snad.sniffer.logging

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

internal interface NetworkDataRepository {
    val requests: Flow<List<NetworkRequest>>
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
    private val updates = Channel<List<NetworkRequest>>(capacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    override val requests = updates.consumeAsFlow()

    override fun add(data: NetworkRequest) {
        networkRequests.add(data)
        updates.trySend(networkRequests)
    }

    override fun clear() {
        networkRequests.clear()
        updates.trySend(networkRequests)
    }
}
