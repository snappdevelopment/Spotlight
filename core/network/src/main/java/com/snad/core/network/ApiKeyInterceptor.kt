package com.snad.core.network

import okhttp3.Interceptor
import okhttp3.Response

internal class ApiKeyInterceptor: Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val httpUrl = request.url.newBuilder()
            .addQueryParameter("api_key", BuildConfig.API_KEY)
            .build()
        request = request.newBuilder()
            .url(httpUrl)
            .build()

        return chain.proceed(request)
    }
}