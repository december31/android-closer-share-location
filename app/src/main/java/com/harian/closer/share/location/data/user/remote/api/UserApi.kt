package com.harian.closer.share.location.data.user.remote.api

import com.harian.closer.share.location.data.common.utils.WrappedListResponse
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.post.remote.dto.PostDTO
import com.harian.closer.share.location.data.user.remote.dto.UserDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserApi {
    @GET("user")
    suspend fun getUserInformation(): Response<WrappedResponse<UserDTO>>

    @GET("user/{id}")
    suspend fun getUserInformation(@Path("id") userId: Int): Response<WrappedResponse<UserDTO>>

    @GET("user/friends")
    suspend fun getFriends(): Response<WrappedListResponse<UserDTO>>

    @GET("user/{id}/friends")
    suspend fun getFriends(@Path("id") userId: Int): Response<WrappedListResponse<UserDTO>>

    @GET("user/posts")
    suspend fun getPosts(): Response<WrappedListResponse<PostDTO>>

    @GET("user/{id}/posts")
    suspend fun getPosts(@Path("id") userId: Int): Response<WrappedListResponse<PostDTO>>

    @POST("friend/request")
    suspend fun sendFriendRequest(user: UserDTO): Response<WrappedResponse<UserDTO>>
}
