package com.harian.closer.share.location.data.post.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.harian.closer.share.location.data.common.utils.WrappedListResponse
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.post.remote.api.PostApi
import com.harian.closer.share.location.data.post.remote.dto.CreatePostRequest
import com.harian.closer.share.location.data.post.remote.dto.PostResponse
import com.harian.closer.share.location.domain.comment.entity.CommentEntity
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.post.PostRepository
import com.harian.closer.share.location.domain.post.entity.PostEntity
import com.harian.closer.share.location.domain.user.entity.UserEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PostRepositoryImpl(private val postApi: PostApi) : PostRepository {
    override suspend fun createPost(postRequest: CreatePostRequest): Flow<BaseResult<PostEntity, WrappedResponse<PostResponse>>> {
        return flow {
            delay(1000)
            val response = postApi.createPost(postRequest)
            if (response.isSuccessful && response.code() in 200 until 400) {
                val body = response.body()
                val postOwner = body?.data?.owner?.let { owner ->
                    UserEntity(
                        id = owner.id,
                        name = owner.name,
                        avatar = owner.avatar,
                        email = owner.email,
                        gender = owner.gender,
                        description = owner.description,
                    )
                }
                val postEntity = body?.data.let { data ->
                    PostEntity(
                        id = data?.id,
                        title = data?.title,
                        content = data?.content,
                        imageUrls = data?.imageUrls,
                        createdTime = data?.createdTime,
                        lastModified = data?.lastModified,
                        owner = postOwner,
                        likes = listOf(),
                        comments = listOf()
                    )
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

    override suspend fun getPopularPosts(
        page: Int?,
        pageSize: Int?
    ): Flow<BaseResult<List<PostEntity>, WrappedListResponse<PostResponse>>> {
        return flow {
            val response = postApi.getPopularPosts(page, pageSize)
            if (response.isSuccessful && response.code() in 200 until 400) {
                val body = response.body()
                val posts = body?.data?.map { postResponse ->
                    val owner = UserEntity(
                        id = postResponse.owner?.id,
                        name = postResponse.owner?.name,
                        email = postResponse.owner?.email,
                        avatar = postResponse.owner?.avatar,
                        gender = postResponse.owner?.gender,
                        description = postResponse.owner?.description,
                    )
                    val postLikes = postResponse.likes?.map { user ->
                        UserEntity(
                            id = user?.id,
                            name = user?.name,
                            avatar = user?.avatar,
                            email = user?.email,
                            gender = user?.gender,
                            description = user?.description,
                        )
                    }
                    val postComments = postResponse.comments?.map { comment ->
                        CommentEntity(
                            id = comment?.id,
                            content = comment?.content,
                            createdTime = comment?.createTime,
                            owner = UserEntity(
                                id = comment?.owner?.id,
                                name = comment?.owner?.name,
                                avatar = comment?.owner?.avatar,
                                email = comment?.owner?.email,
                                gender = comment?.owner?.gender,
                                description = comment?.owner?.description,
                            )
                        )
                    }
                    PostEntity(
                        id = postResponse.id,
                        title = postResponse.title,
                        content = postResponse.content,
                        imageUrls = postResponse.imageUrls,
                        createdTime = postResponse.createdTime,
                        lastModified = postResponse.lastModified,
                        owner = owner,
                        likes = postLikes,
                        comments = postComments
                    )
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
                val owner = UserEntity(
                    id = postResponse?.owner?.id,
                    name = postResponse?.owner?.name,
                    email = postResponse?.owner?.email,
                    avatar = postResponse?.owner?.avatar,
                    gender = postResponse?.owner?.gender,
                    description = postResponse?.owner?.description,
                )
                val postLikes = postResponse?.likes?.map { user ->
                    UserEntity(
                        id = user?.id,
                        name = user?.name,
                        avatar = user?.avatar,
                        email = user?.email,
                        gender = user?.gender,
                        description = user?.description,
                    )
                }
                val postComments = postResponse?.comments?.map { comment ->
                    CommentEntity(
                        id = comment?.id,
                        content = comment?.content,
                        createdTime = comment?.createTime,
                        owner = UserEntity(
                            id = comment?.owner?.id,
                            name = comment?.owner?.name,
                            avatar = comment?.owner?.avatar,
                            email = comment?.owner?.email,
                            gender = comment?.owner?.gender,
                            description = comment?.owner?.description,
                        )
                    )
                }
                val postEntity = PostEntity(
                    id = postResponse?.id,
                    title = postResponse?.title,
                    content = postResponse?.content,
                    imageUrls = postResponse?.imageUrls,
                    createdTime = postResponse?.createdTime,
                    lastModified = postResponse?.lastModified,
                    owner = owner,
                    likes = postLikes,
                    comments = postComments
                )
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
