package com.harian.closer.share.location.data.login.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.harian.closer.share.location.data.common.utils.Token
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.login.remote.api.LoginApi
import com.harian.closer.share.location.data.login.remote.dto.LoginRequest
import com.harian.closer.share.location.data.login.remote.dto.LoginResponse
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.login.LoginRepository
import com.harian.closer.share.location.domain.login.entity.LoginEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(private val loginApi: LoginApi) : LoginRepository {
    override suspend fun login(loginRequest: LoginRequest): Flow<BaseResult<LoginEntity, WrappedResponse<LoginResponse>>> {
        return flow {
            delay(1000) // for loading animation
            val response = loginApi.login(loginRequest)
            if (response.isSuccessful) {
                val body = response.body()
                val loginEntity = body?.data.let { data ->
                    LoginEntity(
                        id = data?.id,
                        name = data?.name,
                        email = data?.email,
                        gender = data?.gender,
                        description = data?.description,
                        token = Token(data?.accessToken, data?.refreshToken)
                    )
                }
                emit(BaseResult.Success(loginEntity))
            } else {
                val type = object : TypeToken<WrappedResponse<LoginResponse>>() {}.type
                val error: WrappedResponse<LoginResponse> =
                    Gson().fromJson(response.errorBody()!!.charStream(), type)
                error.code = response.code()
                emit(BaseResult.Error(error))
            }
        }
    }
}
