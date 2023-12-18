package com.harian.closer.share.location.domain.post

import com.harian.closer.share.location.data.common.utils.WrappedListResponse
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.post.remote.dto.CreatePostRequest
import com.harian.closer.share.location.data.post.remote.dto.PostResponse
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.post.entity.PostEntity
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    suspend fun createPost(postRequest: CreatePostRequest): Flow<BaseResult<PostEntity, WrappedResponse<PostResponse>>>
    suspend fun getPopularPosts(page: Int?, pageSize: Int?): Flow<BaseResult<List<PostEntity>, WrappedListResponse<PostResponse>>>
    suspend fun getPostById(id: Int): Flow<BaseResult<PostEntity, WrappedResponse<PostResponse>>>
}
