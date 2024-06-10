package com.harian.closer.share.location.domain.login.usecase

import com.harian.closer.share.location.data.authenticate.remote.dto.AuthenticateResponse
import com.harian.closer.share.location.data.authenticate.remote.dto.OtpAuthenticateRequest
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.login.AuthenticateRepository
import com.harian.closer.share.location.domain.login.entity.AuthenticateEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OtpAuthenticateUseCase @Inject constructor(private val repository: AuthenticateRepository) {
    suspend fun execute(otpAuthenticateRequest: OtpAuthenticateRequest): Flow<BaseResult<AuthenticateEntity, WrappedResponse<AuthenticateResponse>>> {
        return repository.authenticate(otpAuthenticateRequest)
    }
}
