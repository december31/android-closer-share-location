package com.harian.closer.share.location.data.authenticate.remote.api

import com.harian.closer.share.location.data.authenticate.remote.dto.AuthenticateRequest
import com.harian.closer.share.location.data.authenticate.remote.dto.AuthenticateResponse
import com.harian.closer.share.location.data.authenticate.remote.dto.OtpAuthenticateRequest
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthenticateApi {
    @POST("api/v1/auth/authenticate")
    suspend fun authenticate(@Body request: AuthenticateRequest): Response<WrappedResponse<AuthenticateResponse>>

    @POST("api/v1/auth/otp-authenticate")
    suspend fun authenticate(@Body request: OtpAuthenticateRequest): Response<WrappedResponse<AuthenticateResponse>>
}
