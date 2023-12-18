package com.harian.closer.share.location.data.post.remote.api

import com.harian.closer.share.location.data.common.utils.WrappedListResponse
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.post.remote.dto.CommentRequest
import com.harian.closer.share.location.data.post.remote.dto.CommentResponse
import com.harian.closer.share.location.data.post.remote.dto.CreatePostRequest
import com.harian.closer.share.location.data.post.remote.dto.PostResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface PostApi {
    @POST("api/v1/post/create")
    suspend fun createPost(@Body createPostRequest: CreatePostRequest): Response<WrappedResponse<PostResponse>>

    @POST("api/v1/post/comment")
    suspend fun createComment(@Body commentRequest: CommentRequest, @Query("post-id") postId: Int): Response<WrappedResponse<CommentResponse>>

    @GET("api/v1/post/popular")
    suspend fun getPopularPosts(
        @Query("page") page: Int? = null,
        @Query("page-size") pageSize: Int? = null
    ): Response<WrappedListResponse<PostResponse>>

    @GET("api/v1/post")
    suspend fun getPostById(@Query("id") postId: Int? = null): Response<WrappedResponse<PostResponse>>

}
