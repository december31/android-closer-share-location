package com.harian.closer.share.location.data.request.otp.remote.api

import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.request.otp.remote.dto.RequestOtpRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RequestOtpApi {
    @POST("api/v1/auth/request-otp")
    suspend fun requestOtp(@Body requestOtpRequest: RequestOtpRequest): Response<WrappedResponse<Unit>>
}
