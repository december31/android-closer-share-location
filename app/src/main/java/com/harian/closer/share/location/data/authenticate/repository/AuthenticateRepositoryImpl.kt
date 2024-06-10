package com.harian.closer.share.location.data.authenticate.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.harian.closer.share.location.data.authenticate.remote.api.AuthenticateApi
import com.harian.closer.share.location.data.authenticate.remote.dto.AuthenticateRequest
import com.harian.closer.share.location.data.authenticate.remote.dto.AuthenticateResponse
import com.harian.closer.share.location.data.authenticate.remote.dto.OtpAuthenticateRequest
import com.harian.closer.share.location.data.common.utils.Token
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.login.AuthenticateRepository
import com.harian.closer.share.location.domain.login.entity.AuthenticateEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthenticateRepositoryImpl @Inject constructor(private val authenticateApi: AuthenticateApi) :
    AuthenticateRepository {
    override suspend fun authenticate(authenticateRequest: AuthenticateRequest): Flow<BaseResult<AuthenticateEntity, WrappedResponse<AuthenticateResponse>>> {
        return flow {
            val response = authenticateApi.authenticate(authenticateRequest)
            if (response.isSuccessful) {
                val body = response.body()
                val authenticateEntity = body?.data.let { data ->
                    AuthenticateEntity(
                        token = Token(data?.accessToken, data?.refreshToken)
                    )
                }
                emit(BaseResult.Success(authenticateEntity))
            } else {
                val type = object : TypeToken<WrappedResponse<AuthenticateResponse>>() {}.type
                val error: WrappedResponse<AuthenticateResponse> =
                    Gson().fromJson(response.errorBody()!!.charStream(), type)
                error.code = response.code()
                emit(BaseResult.Error(error))
            }
        }
    }

    override suspend fun authenticate(otpAuthenticateRequest: OtpAuthenticateRequest): Flow<BaseResult<AuthenticateEntity, WrappedResponse<AuthenticateResponse>>> {
        return flow {
            val response = authenticateApi.authenticate(otpAuthenticateRequest)
            if (response.isSuccessful) {
                val body = response.body()
                val authenticateEntity = body?.data.let { data ->
                    AuthenticateEntity(
                        token = Token(data?.accessToken, data?.refreshToken)
                    )
                }
                emit(BaseResult.Success(authenticateEntity))
            } else {
                val type = object : TypeToken<WrappedResponse<AuthenticateResponse>>() {}.type
                val error: WrappedResponse<AuthenticateResponse> =
                    Gson().fromJson(response.errorBody()!!.charStream(), type)
                error.code = response.code()
                emit(BaseResult.Error(error))
            }
        }
    }

    override suspend fun authenticate(): Flow<BaseResult<AuthenticateEntity, WrappedResponse<AuthenticateResponse>>> {
        return flow {
            val response = authenticateApi.authenticate()
            if (response.isSuccessful) {
                val body = response.body()
                val authenticateEntity = body?.data.let { data ->
                    AuthenticateEntity(
                        token = Token(data?.accessToken, data?.refreshToken)
                    )
                }
                emit(BaseResult.Success(authenticateEntity))
            } else {
                val type = object : TypeToken<WrappedResponse<AuthenticateResponse>>() {}.type
                val error: WrappedResponse<AuthenticateResponse> =
                    Gson().fromJson(response.errorBody()!!.charStream(), type)
                error.code = response.code()
                emit(BaseResult.Error(error))
            }
        }
    }
}
