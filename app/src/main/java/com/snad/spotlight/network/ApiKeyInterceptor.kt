package com.snad.spotlight.network

import com.snad.spotlight.apiKey
import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyInterceptor: Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val httpUrl = request.url().newBuilder()
            .addQueryParameter("api_key", apiKey)
            .build()
        request = request.newBuilder()
            .url(httpUrl)
            .build()

        return chain.proceed(request)
    }
}