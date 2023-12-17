package com.harian.closer.share.location.domain.refresh.token.usecase

import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.refresh.token.remote.dto.RefreshTokenResponse
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.refresh.token.RefreshTokenRepository
import com.harian.closer.share.location.domain.refresh.token.entity.RefreshTokenEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RefreshTokenUseCase @Inject constructor(private val refreshTokenRepository: RefreshTokenRepository){
    suspend fun execute(): Flow<BaseResult<RefreshTokenEntity, WrappedResponse<RefreshTokenResponse>>> {
        return refreshTokenRepository.refresh()
    }
}
