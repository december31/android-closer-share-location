package com.harian.closer.share.location.domain.login

import com.harian.closer.share.location.data.authenticate.remote.dto.AuthenticateRequest
import com.harian.closer.share.location.data.authenticate.remote.dto.AuthenticateResponse
import com.harian.closer.share.location.data.authenticate.remote.dto.OtpAuthenticateRequest
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.login.entity.AuthenticateEntity
import kotlinx.coroutines.flow.Flow

interface AuthenticateRepository {
    suspend fun authenticate(authenticateRequest: AuthenticateRequest): Flow<BaseResult<AuthenticateEntity, WrappedResponse<AuthenticateResponse>>>
    suspend fun authenticate(otpAuthenticateRequest: OtpAuthenticateRequest): Flow<BaseResult<AuthenticateEntity, WrappedResponse<AuthenticateResponse>>>
}
