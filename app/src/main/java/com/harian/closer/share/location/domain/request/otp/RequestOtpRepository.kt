package com.harian.closer.share.location.domain.request.otp

import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.request.otp.remote.dto.RequestOtpRequest
import com.harian.closer.share.location.domain.common.base.BaseResult
import kotlinx.coroutines.flow.Flow

interface RequestOtpRepository {
    suspend fun requestOtp(requestOtpRequest: RequestOtpRequest): Flow<BaseResult<Unit, WrappedResponse<Unit>>>
}
