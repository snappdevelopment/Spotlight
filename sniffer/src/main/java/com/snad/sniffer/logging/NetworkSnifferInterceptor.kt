package com.snad.sniffer.logging

import android.util.Log
import java.nio.charset.Charset
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import okio.GzipSource
import okio.buffer

internal class NetworkSnifferInterceptor(
    private val networkDataRepository: NetworkDataRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        //Todo: remove
        Log.d("NetworkSniffer", request.url.toString())

        val timeStamp = System.currentTimeMillis()
        val url = request.url.toString()
        val method = request.method
        var requestBodyString: String? = null

        val requestBody = request.body
        if (requestBody != null) {
            runCatching {
                val buffer = Buffer()
                requestBody.writeTo(buffer)
                requestBodyString = buffer.readString(Charset.forName("UTF8"))
            }
        }

        val startTime = System.nanoTime()
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            throw e
        }

        val durationMillis = (System.nanoTime() - startTime) / 1000
        val statusCode = response.code
        var responseBodyString: String? = null

        val responseBody = response.body
        if (responseBody != null && response.headers["Content-Encoding"].equals("gzip", ignoreCase = true)) {
            runCatching {
                val buffer = GzipSource(responseBody.source()).buffer()
                responseBodyString = buffer.readString(Charset.forName("UTF8"))
            }
        }

        val data = """
            Timestamp: $timeStamp
            Url: $url
            method: $method
            requestBody: $requestBodyString
            statusCode: $statusCode
            duration: $durationMillis
            responseBody: $responseBodyString
        """.trimIndent()

        networkDataRepository.add(data)

//        NetworkRequest(
//            timestampMillis = timeStamp,
//            url = url,
//            body = null,
//            method = method,
//            statusCode = statusCode,
//            size = ,
//            duration = durationMillis,
//            response = responseBody,
//        )

        return response
    }
}
