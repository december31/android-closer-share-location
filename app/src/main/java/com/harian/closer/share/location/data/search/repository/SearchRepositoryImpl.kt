package com.harian.closer.share.location.data.search.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.harian.closer.share.location.data.common.utils.WrappedListResponse
import com.harian.closer.share.location.data.post.remote.dto.PostDTO
import com.harian.closer.share.location.data.search.remote.api.SearchApi
import com.harian.closer.share.location.data.user.remote.dto.UserDTO
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.post.entity.PostEntity
import com.harian.closer.share.location.domain.search.SearchRepository
import com.harian.closer.share.location.domain.user.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchRepositoryImpl(private val searchApi: SearchApi) : SearchRepository {
    override suspend fun searchUsers(query: String): Flow<BaseResult<List<UserEntity>, WrappedListResponse<UserDTO>>> {
        return flow {
            val response = searchApi.searchUsers(query)
            if (response.isSuccessful && response.code() in 200 until 400) {
                response.body()?.data?.let {
                    val result = it.map { userDTO -> userDTO.toEntity() }
                    emit(BaseResult.Success(result))
                }
            } else {
                val type = object : TypeToken<WrappedListResponse<UserDTO>>() {}.type
                val error = Gson().fromJson<WrappedListResponse<UserDTO>>(response.errorBody()?.charStream(), type)
                emit(BaseResult.Error(error))
            }
        }
    }

    override suspend fun searchPosts(query: String): Flow<BaseResult<List<PostEntity>, WrappedListResponse<PostDTO>>> {
        return flow {
            val response = searchApi.searchPosts(query)
            if (response.isSuccessful && response.code() in 200 until 400) {
                response.body()?.data?.let {
                    val result = it.map { postDTO -> postDTO.toEntity() }
                    emit(BaseResult.Success(result))
                }
            } else {
                val type = object : TypeToken<WrappedListResponse<PostDTO>>() {}.type
                val error = Gson().fromJson<WrappedListResponse<PostDTO>>(response.errorBody()?.charStream(), type)
                emit(BaseResult.Error(error))
            }
        }
    }
}
