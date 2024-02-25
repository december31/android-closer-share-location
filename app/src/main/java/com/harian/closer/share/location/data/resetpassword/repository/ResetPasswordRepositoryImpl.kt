package com.harian.closer.share.location.data.resetpassword.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.resetpassword.remote.api.ResetPasswordApi
import com.harian.closer.share.location.data.resetpassword.remote.dto.ResetPasswordRequest
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.resetpassword.ResetPasswordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ResetPasswordRepositoryImpl(private val resetPasswordApi: ResetPasswordApi) : ResetPasswordRepository {
    override suspend fun resetPassword(resetPasswordRequest: ResetPasswordRequest): Flow<BaseResult<Unit, WrappedResponse<Unit>>> {
        return flow {
            val response = resetPasswordApi.resetPassword(resetPasswordRequest)
            if (response.isSuccessful && response.code() in 200 until 400) {
                emit(BaseResult.Success(Unit))
            } else {
                val type = object : TypeToken<WrappedResponse<Unit>>() {}.type
                val error =
                    Gson().fromJson<WrappedResponse<Unit>>(response.errorBody()?.charStream(), type)
                emit(BaseResult.Error(error))
            }
        }
    }
}
