package com.harian.closer.share.location.data.refresh.token.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.harian.closer.share.location.data.common.utils.Token
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.refresh.token.remote.api.RefreshTokenApi
import com.harian.closer.share.location.data.refresh.token.remote.dto.RefreshTokenResponse
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.refresh.token.RefreshTokenRepository
import com.harian.closer.share.location.domain.refresh.token.entity.RefreshTokenEntity
import com.harian.closer.share.location.platform.SharedPrefs
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RefreshTokenRepositoryImpl(private val refreshTokenApi: RefreshTokenApi, private val sharedPrefs: SharedPrefs) :
    RefreshTokenRepository {
    override suspend fun refresh(): Flow<BaseResult<RefreshTokenEntity, WrappedResponse<RefreshTokenResponse>>> {
        return flow {
            delay(2000)
            val response = refreshTokenApi.refreshToken("Bearer " + sharedPrefs.getRefreshToken())
            if (response.isSuccessful && response.code() in 200 until 400) {
                val body = response.body()
                val refreshTokenEntity = body?.data.let { data ->
                    RefreshTokenEntity(
                        id = data?.id,
                        name = data?.name,
                        email = data?.email,
                        gender = data?.gender,
                        description = data?.description,
                        token = Token(data?.accessToken, data?.refreshToken)
                    )
                }
                emit(BaseResult.Success(refreshTokenEntity))
            } else {
                val type = object : TypeToken<WrappedResponse<RefreshTokenResponse>>() {}.type
                val error: WrappedResponse<RefreshTokenResponse> =
                    Gson().fromJson(response.errorBody()!!.charStream(), type)
                error.code = response.code()
                emit(BaseResult.Error(error))
            }
        }
    }
}
