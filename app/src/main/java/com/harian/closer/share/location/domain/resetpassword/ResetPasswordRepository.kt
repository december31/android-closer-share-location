package com.harian.closer.share.location.domain.resetpassword

import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.resetpassword.remote.dto.ResetPasswordRequest
import com.harian.closer.share.location.domain.common.base.BaseResult
import kotlinx.coroutines.flow.Flow

interface ResetPasswordRepository {
    suspend fun resetPassword(resetPasswordRequest: ResetPasswordRequest): Flow<BaseResult<Unit, WrappedResponse<Unit>>>
}
