package com.harian.closer.share.location.data.common.utils

import com.harian.closer.share.location.platform.SharedPrefs
import okhttp3.Interceptor
import okhttp3.Response

class RequestInterceptor(private val pref: SharedPrefs) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = pref.getToken()
        val newRequest = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        return chain.proceed(newRequest)
    }
}
