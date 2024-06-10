package com.harian.closer.share.location.data.search.remote.api

import com.harian.closer.share.location.data.common.utils.WrappedListResponse
import com.harian.closer.share.location.data.post.remote.dto.PostDTO
import com.harian.closer.share.location.data.user.remote.dto.UserDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {
    @GET("api/v1/user/search")
    suspend fun searchUsers(@Query("query") query: String): Response<WrappedListResponse<UserDTO>>

    @GET("api/v1/post/search")
    suspend fun searchPosts(@Query("query") query: String): Response<WrappedListResponse<PostDTO>>

}
