package com.harian.closer.share.location.data.user.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.harian.closer.share.location.data.common.utils.WrappedListResponse
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.post.remote.dto.PostResponse
import com.harian.closer.share.location.data.user.remote.api.UserApi
import com.harian.closer.share.location.data.user.remote.dto.UserResponse
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.post.entity.PostEntity
import com.harian.closer.share.location.domain.user.UserRepository
import com.harian.closer.share.location.domain.user.entity.UserEntity
import com.harian.closer.share.location.utils.ResponseUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserRepositoryImpl(private val userApi: UserApi, private val responseUtil: ResponseUtil) : UserRepository {

    private var userInformation: BaseResult.Success<UserEntity>? = null

    override suspend fun getUserInformation(): Flow<BaseResult<UserEntity, WrappedResponse<UserResponse>>> {
        return flow {
            if (userInformation == null) {
                val response = userApi.getUserInformation()
                if (response.isSuccessful && response.code() in 200 until 400) {
                    val body = response.body()
                    val userEntity = body?.data.let { data ->
                        responseUtil.buildUserEntity(data)
                    }
                    userInformation = BaseResult.Success(userEntity)
                    emit(userInformation!!)
                } else {
                    val type = object : TypeToken<WrappedResponse<UserResponse>>() {}.type
                    val error =
                        Gson().fromJson<WrappedResponse<UserResponse>>(response.errorBody()?.charStream(), type)
                    emit(BaseResult.Error(error))
                }
            } else {
                delay(500)
                emit(userInformation!!)
            }
        }
    }

    override suspend fun getFriends(): Flow<BaseResult<List<UserEntity>, WrappedListResponse<UserResponse>>> {
        return flow {
            val response = userApi.getFriends()
            if (response.isSuccessful && response.code() in 200 until 400) {
                val body = response.body()
                val friends = body?.data?.let { data ->
                    data.map { friend ->
                        responseUtil.buildUserEntity(friend)
                    }
                } ?: listOf()
                emit(BaseResult.Success(friends))
            } else {
                val type = object : TypeToken<WrappedListResponse<UserResponse>>() {}.type
                val error =
                    Gson().fromJson<WrappedListResponse<UserResponse>>(response.errorBody()?.charStream(), type)
                emit(BaseResult.Error(error))
            }
        }
    }

    override suspend fun getPosts(): Flow<BaseResult<List<PostEntity>, WrappedListResponse<PostResponse>>> {
        return flow {
            val response = userApi.getPosts()
            if (response.isSuccessful && response.code() in 200 until 400) {
                val body = response.body()
                val posts = body?.data?.map { postResponse ->
                    responseUtil.buildPostEntity(postResponse)
                } ?: listOf()
                emit(BaseResult.Success(posts))
            } else {
                val type = object : TypeToken<WrappedListResponse<PostResponse>>() {}.type
                val error =
                    Gson().fromJson<WrappedListResponse<PostResponse>>(response.errorBody()?.charStream(), type)
                emit(BaseResult.Error(error))
            }
        }
    }
}
