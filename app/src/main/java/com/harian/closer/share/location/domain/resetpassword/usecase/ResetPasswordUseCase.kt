package com.harian.closer.share.location.domain.resetpassword.usecase

import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.resetpassword.remote.dto.ResetPasswordRequest
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.resetpassword.ResetPasswordRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(private val resetPasswordRepository: ResetPasswordRepository) {
    suspend fun execute(resetPasswordRequest: ResetPasswordRequest): Flow<BaseResult<Unit, WrappedResponse<Unit>>> {
        return resetPasswordRepository.resetPassword(resetPasswordRequest)
    }
}
