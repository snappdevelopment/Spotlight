package com.snad.sniffer.logging

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

internal interface NetworkDataRepository {
    /**
     * A flow which emits updates of the list of [NetworkRequest].
     */
    val requests: Flow<List<NetworkRequest>>

    /**
     * Adds a new [NetworkRequest] to the list of requests.
     */
    fun add(data: NetworkRequest)

    /**
     * Searches and replaces an already existing [NetworkRequest].
     * Fails silently, if the request couldn't be found in the list.
     */
    fun update(data: NetworkRequest)

    /**
     * Deletes all [NetworkRequest] from the list.
     */
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

    override fun update(data: NetworkRequest) {
        val requests = networkRequests
        val index = requests.indexOfFirst { it.id == data.id }
        if(index != -1) {
            requests.removeAt(index)
            requests.add(index, data)
            updates.trySend(requests)
        }
    }

    override fun clear() {
        networkRequests.clear()
        updates.trySend(networkRequests)
    }
}
