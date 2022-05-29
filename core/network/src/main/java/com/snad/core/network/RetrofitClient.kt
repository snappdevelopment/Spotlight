package com.snad.core.network

import com.snad.sniffer.NetworkSniffer
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import javax.inject.Inject

internal class RetrofitClient @Inject constructor(
    private val cacheDir: File
) {
    fun get(): Retrofit {
        val cacheSize = 10 * 1024 * 1024 // 10 MB
        val cache = Cache(cacheDir, cacheSize.toLong())

        val networkSnifferInterceptor = NetworkSniffer.Factory.get().getInterceptor()

        val httpClient = OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(ApiKeyInterceptor())
            .addInterceptor(networkSnifferInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .client(httpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }
}