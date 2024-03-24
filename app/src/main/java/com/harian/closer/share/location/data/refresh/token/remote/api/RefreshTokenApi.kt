package com.harian.closer.share.location.data.refresh.token.remote.api

import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.refresh.token.remote.dto.RefreshTokenResponse
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.POST

interface RefreshTokenApi {
    @POST("auth/refresh-token")
    suspend fun refreshToken(@Header("RefreshToken") refreshToken: String): Response<WrappedResponse<RefreshTokenResponse>>
}
