package com.harian.closer.share.location.data.user.remote.api

import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.user.remote.dto.UserResponse
import retrofit2.Response
import retrofit2.http.GET

interface UserApi {
    @GET("api/v1/user")
    suspend fun getUserInformation(): Response<WrappedResponse<UserResponse>>
}
