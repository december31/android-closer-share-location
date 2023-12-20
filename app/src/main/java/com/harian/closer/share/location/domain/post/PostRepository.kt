package com.harian.closer.share.location.domain.post

import com.harian.closer.share.location.data.common.utils.WrappedListResponse
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.post.remote.dto.CommentRequest
import com.harian.closer.share.location.data.post.remote.dto.CommentResponse
import com.harian.closer.share.location.data.post.remote.dto.CreatePostRequest
import com.harian.closer.share.location.data.post.remote.dto.PostResponse
import com.harian.closer.share.location.domain.comment.entity.CommentEntity
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.post.entity.PostEntity
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface PostRepository {
    suspend fun createPost(parts: List<MultipartBody.Part>?, body: RequestBody): Flow<BaseResult<PostEntity, WrappedResponse<PostResponse>>>
    suspend fun createComment(commentRequest: CommentRequest, postId: Int): Flow<BaseResult<CommentEntity, WrappedResponse<CommentResponse>>>
    suspend fun getPopularPosts(page: Int?, pageSize: Int?): Flow<BaseResult<List<PostEntity>, WrappedListResponse<PostResponse>>>
    suspend fun getPostById(id: Int): Flow<BaseResult<PostEntity, WrappedResponse<PostResponse>>>
}
