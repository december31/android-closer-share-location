package com.harian.closer.share.location.data.resetpassword.remote.api

import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.resetpassword.remote.dto.ResetPasswordRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PATCH

interface ResetPasswordApi {
    @PATCH("user/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<WrappedResponse<Unit>>
}
