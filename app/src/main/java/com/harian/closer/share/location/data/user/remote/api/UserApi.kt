package com.harian.closer.share.location.data.user.remote.api

import com.harian.closer.share.location.data.common.utils.WrappedListResponse
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.post.remote.dto.PostDTO
import com.harian.closer.share.location.data.user.remote.dto.DeviceDTO
import com.harian.closer.share.location.data.user.remote.dto.FriendRequestDTO
import com.harian.closer.share.location.data.user.remote.dto.FriendsResponse
import com.harian.closer.share.location.data.user.remote.dto.UserDTO
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApi {
    @GET("api/v1/user")
    suspend fun getUserInformation(): Response<WrappedResponse<UserDTO>>

    @GET("api/v1/user/{id}")
    suspend fun getUserInformation(@Path("id") userId: Long): Response<WrappedResponse<UserDTO>>

    @GET("api/v1/user/friends")
    suspend fun getFriends(@Query("page") page: Int, @Query("page-size") limit: Int): Response<WrappedResponse<FriendsResponse>>

    @GET("api/v1/user/{id}/friends")
    suspend fun getFriends(@Path("id") userId: Long, @Query("page") page: Int, @Query("page-size") limit: Int): Response<WrappedResponse<FriendsResponse>>

    @GET("api/v1/user/posts")
    suspend fun getPosts(): Response<WrappedListResponse<PostDTO>>

    @GET("api/v1/user/{id}/posts")
    suspend fun getPosts(@Path("id") userId: Long): Response<WrappedListResponse<PostDTO>>

    @POST("api/v1/user/friend/request")
    suspend fun sendFriendRequest(@Body user: UserDTO): Response<WrappedResponse<UserDTO>>

    @GET("api/v1/user/friend/requests")
    suspend fun getFriendRequest(): Response<WrappedListResponse<FriendRequestDTO>>

    @POST("api/v1/user/device/update")
    suspend fun updateDeviceInformation(@Body device: DeviceDTO): Response<WrappedResponse<DeviceDTO>>

    @POST("api/v1/user/friend/accept")
    suspend fun acceptFriendRequest(@Body user: UserDTO): Response<WrappedResponse<UserDTO>>

    @POST("api/v1/user/friend/deny")
    suspend fun denyFriendRequest(@Body user: UserDTO): Response<WrappedResponse<UserDTO>>

    @Multipart
    @PATCH("api/v1/user/update-avatar")
    suspend fun updateAvatar(@Part image: MultipartBody.Part): Response<WrappedResponse<UserDTO>>

    @PATCH("api/v1/user/update")
    suspend fun updateInformation(@Body user: UserDTO): Response<WrappedResponse<UserDTO>>
}
