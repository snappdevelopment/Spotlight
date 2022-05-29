package com.snad.sniffer

import android.content.Context
import com.snad.sniffer.logging.NetworkDataRepository
import com.snad.sniffer.logging.NetworkSnifferInterceptor
import okhttp3.Interceptor

interface NetworkSniffer {
    fun getInterceptor(): Interceptor

    fun start(context: Context)

    object Factory {
        internal val repository = NetworkDataRepository.get()

        fun get(): NetworkSniffer {
            return AndroidNetworkSniffer(repository)
        }
    }
}

private class AndroidNetworkSniffer(
    private val repository: NetworkDataRepository
): NetworkSniffer {

    override fun getInterceptor(): Interceptor {
        return NetworkSnifferInterceptor(repository)
    }

    override fun start(context: Context) {
        NetworkSnifferNotification.send(context)
    }
}