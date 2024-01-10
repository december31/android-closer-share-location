package com.harian.closer.share.location.data.post.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.harian.closer.share.location.data.common.utils.WrappedListResponse
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.post.remote.api.PostApi
import com.harian.closer.share.location.data.post.remote.dto.CommentRequest
import com.harian.closer.share.location.data.post.remote.dto.CommentResponse
import com.harian.closer.share.location.data.post.remote.dto.PostResponse
import com.harian.closer.share.location.domain.comment.entity.CommentEntity
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.post.PostRepository
import com.harian.closer.share.location.domain.post.entity.PostEntity
import com.harian.closer.share.location.utils.ResponseUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

class PostRepositoryImpl(private val postApi: PostApi, private val responseUtil: ResponseUtil) : PostRepository {
    override suspend fun createPost(
        parts: List<MultipartBody.Part>?,
        body: RequestBody
    ): Flow<BaseResult<PostEntity, WrappedResponse<PostResponse>>> {
        return flow {
            val response = postApi.createPost(parts, body)
            if (response.isSuccessful && response.code() in 200 until 400) {
                val postEntity = response.body()?.data.let { data ->
                    responseUtil.buildPostEntity(data)
                }
                emit(BaseResult.Success(postEntity))
            } else {
                val type = object : TypeToken<WrappedResponse<PostResponse>>() {}.type
                val error: WrappedResponse<PostResponse> =
                    Gson().fromJson(response.errorBody()!!.charStream(), type)
                error.code = response.code()
                emit(BaseResult.Error(error))
            }
        }
    }

    override suspend fun createComment(
        commentRequest: CommentRequest,
        postId: Int
    ): Flow<BaseResult<CommentEntity, WrappedResponse<CommentResponse>>> {
        return flow {
            val response = postApi.commentPost(commentRequest, postId)
            if (response.isSuccessful && response.code() in 200 until 400) {
                val body = response.body()
                val commentEntity = body?.data.let { data ->
                    responseUtil.buildCommentEntity(data)
                }
                emit(BaseResult.Success(commentEntity))
            } else {
                val type = object : TypeToken<WrappedResponse<CommentResponse>>() {}.type
                val err = Gson().fromJson<WrappedResponse<CommentResponse>>(response.errorBody()!!.charStream(), type)!!
                err.code = response.code()
                emit(BaseResult.Error(err))
            }
        }
    }

    override suspend fun getPopularPosts(
        page: Int?,
        pageSize: Int?
    ): Flow<BaseResult<List<PostEntity>, WrappedListResponse<PostResponse>>> {
        return flow {
            val response = postApi.getPopularPosts(page, pageSize)
            if (response.isSuccessful && response.code() in 200 until 400) {
                val body = response.body()
                val posts = body?.data?.map { postResponse ->
                    responseUtil.buildPostEntity(postResponse)
                } ?: listOf()
                emit(BaseResult.Success(posts))
            } else {
                val type = object : TypeToken<WrappedListResponse<PostResponse>>() {}.type
                val err = Gson().fromJson<WrappedListResponse<PostResponse>>(response.errorBody()!!.charStream(), type)!!
                err.code = response.code()
                emit(BaseResult.Error(err))
            }
        }
    }

    override suspend fun getPostById(id: Int): Flow<BaseResult<PostEntity, WrappedResponse<PostResponse>>> {
        return flow {
            val response = postApi.getPostById(id)
            if (response.isSuccessful && response.code() in 200 until 400) {
                val postResponse = response.body()?.data
                val postEntity = responseUtil.buildPostEntity(postResponse)
                emit(BaseResult.Success(postEntity))
            } else {
                val type = object : TypeToken<WrappedResponse<PostResponse>>() {}.type
                val err = Gson().fromJson<WrappedResponse<PostResponse>>(response.errorBody()!!.charStream(), type)!!
                err.code = response.code()
                emit(BaseResult.Error(err))
            }
        }
    }

    override suspend fun likePost(id: Int): Flow<BaseResult<PostEntity, WrappedResponse<PostResponse>>> {
        return flow {
            val response = postApi.likePost(id)
            if (response.isSuccessful && response.code() in 200 until 400) {
                val postResponse = response.body()?.data
                val postEntity = responseUtil.buildPostEntity(postResponse)
                emit(BaseResult.Success(postEntity))
            } else {
                val type = object : TypeToken<WrappedResponse<PostResponse>>() {}.type
                val err = Gson().fromJson<WrappedResponse<PostResponse>>(response.errorBody()!!.charStream(), type)!!
                err.code = response.code()
                emit(BaseResult.Error(err))
            }
        }
    }

    override suspend fun unlikePost(id: Int): Flow<BaseResult<PostEntity, WrappedResponse<PostResponse>>> {
        return flow {
            val response = postApi.unlikePost(id)
            if (response.isSuccessful && response.code() in 200 until 400) {
                val postResponse = response.body()?.data
                val postEntity = responseUtil.buildPostEntity(postResponse)
                emit(BaseResult.Success(postEntity))
            } else {
                val type = object : TypeToken<WrappedResponse<PostResponse>>() {}.type
                val err = Gson().fromJson<WrappedResponse<PostResponse>>(response.errorBody()!!.charStream(), type)!!
                err.code = response.code()
                emit(BaseResult.Error(err))
            }
        }
    }
}
