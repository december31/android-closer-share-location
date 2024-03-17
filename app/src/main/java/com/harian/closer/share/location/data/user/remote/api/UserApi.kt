package com.harian.closer.share.location.data.user.remote.api

import com.harian.closer.share.location.data.common.utils.WrappedListResponse
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.post.remote.dto.PostResponse
import com.harian.closer.share.location.data.user.remote.dto.UserResponse
import retrofit2.Response
import retrofit2.http.GET

interface UserApi {
    @GET("api/v1/user")
    suspend fun getUserInformation(): Response<WrappedResponse<UserResponse>>

    @GET("api/v1/user/friends")
    suspend fun getFriends(): Response<WrappedListResponse<UserResponse>>

    @GET("api/v1/user/posts")
    suspend fun getPosts(): Response<WrappedListResponse<PostResponse>>
}
