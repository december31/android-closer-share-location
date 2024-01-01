package com.harian.closer.share.location.data.user.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.user.remote.api.UserApi
import com.harian.closer.share.location.data.user.remote.dto.UserResponse
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.user.UserRepository
import com.harian.closer.share.location.domain.user.entity.UserEntity
import com.harian.closer.share.location.utils.ResponseUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserRepositoryImpl(private val userApi: UserApi, private val responseUtil: ResponseUtil) : UserRepository {
    override suspend fun getUserInformation(): Flow<BaseResult<UserEntity, WrappedResponse<UserResponse>>> {
        return flow {
            val response = userApi.getUserInformation()
            if (response.isSuccessful && response.code() in 200 until 400) {
                val body = response.body()
                val userEntity = body?.data.let { data ->
                    responseUtil.buildUserEntity(data)
                }
                emit(BaseResult.Success(userEntity))
            } else {
                val type = object : TypeToken<WrappedResponse<UserResponse>>() {}.type
                val error =
                    Gson().fromJson<WrappedResponse<UserResponse>>(response.errorBody()?.charStream(), type)
                emit(BaseResult.Error(error))
            }
        }
    }
}
