package com.harian.closer.share.location.data.login.remote.api

import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.login.remote.dto.LoginRequest
import com.harian.closer.share.location.data.login.remote.dto.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApi {
    @POST("api/v1/auth/authenticate")
    suspend fun login(@Body request: LoginRequest): Response<WrappedResponse<LoginResponse>>
}
