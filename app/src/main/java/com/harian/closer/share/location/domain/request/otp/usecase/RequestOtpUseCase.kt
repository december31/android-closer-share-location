package com.harian.closer.share.location.domain.request.otp.usecase

import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.request.otp.remote.dto.RequestOtpRequest
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.request.otp.RequestOtpRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RequestOtpUseCase @Inject constructor(private val repository: RequestOtpRepository) {
    suspend fun execute(request: RequestOtpRequest): Flow<BaseResult<Unit, WrappedResponse<Unit>>> {
        return repository.requestOtp(request)
    }
}
