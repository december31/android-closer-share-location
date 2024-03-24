package com.harian.closer.share.location.data.post.remote.api

import com.harian.closer.share.location.data.common.utils.WrappedListResponse
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.post.remote.dto.CommentRequest
import com.harian.closer.share.location.data.post.remote.dto.CommentResponse
import com.harian.closer.share.location.data.post.remote.dto.ImageResponse
import com.harian.closer.share.location.data.post.remote.dto.PostDTO
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface PostApi {
    @Multipart
    @POST("post/create")
    suspend fun createPost(
        @Part images: List<MultipartBody.Part>?,
        @Part("post") body: RequestBody
    ): Response<WrappedResponse<PostDTO>>

    @POST("post/comment")
    suspend fun commentPost(
        @Body commentRequest: CommentRequest?,
        @Query("id") postId: Int?
    ): Response<WrappedResponse<CommentResponse>>

    @GET("post/popular")
    suspend fun getPopularPosts(
        @Query("page") page: Int? = null,
        @Query("page-size") pageSize: Int? = null
    ): Response<WrappedListResponse<PostDTO>>

    @GET("post")
    suspend fun getPostById(@Query("id") postId: Int? = null): Response<WrappedResponse<PostDTO>>

    @POST("post/like")
    suspend fun likePost(@Query("id") postId: Int? = null): Response<WrappedResponse<PostDTO>>

    @POST("post/unlike")
    suspend fun unlikePost(@Query("id") postId: Int? = null): Response<WrappedResponse<PostDTO>>

    @POST("post/watch")
    suspend fun watchPost(@Query("id") postId: Int? = null): Response<WrappedResponse<PostDTO>>

    @POST("post/image/like")
    suspend fun likeImage(@Query("id") imageId: Int? = null): Response<WrappedResponse<ImageResponse>>

    @POST("post/image/comment")
    suspend fun commentImage(@Query("id") imageId: Int? = null): Response<WrappedResponse<CommentResponse>>
}
