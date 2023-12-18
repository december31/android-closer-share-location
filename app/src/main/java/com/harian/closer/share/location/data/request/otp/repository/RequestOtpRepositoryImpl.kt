package com.harian.closer.share.location.data.request.otp.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.request.otp.remote.api.RequestOtpApi
import com.harian.closer.share.location.data.request.otp.remote.dto.RequestOtpRequest
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.request.api.RequestOtpRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RequestOtpRepositoryImpl(private val requestOtpApi: RequestOtpApi) : RequestOtpRepository {
    override suspend fun requestOtp(requestOtpRequest: RequestOtpRequest): Flow<BaseResult<Unit, WrappedResponse<Unit>>> {
        return flow {
            val response = requestOtpApi.requestOtp(requestOtpRequest)
            if (response.isSuccessful) {
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
