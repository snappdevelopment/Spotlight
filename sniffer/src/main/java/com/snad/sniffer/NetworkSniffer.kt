package com.snad.sniffer

import android.content.Context
import com.snad.sniffer.logging.NetworkDataRepository
import com.snad.sniffer.logging.NetworkSnifferInterceptor
import okhttp3.Interceptor

public interface NetworkSniffer {
    public fun getInterceptor(): Interceptor

    public fun start(context: Context)

    public object Factory {
        internal val repository = NetworkDataRepository.get()

        public fun get(): NetworkSniffer {
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