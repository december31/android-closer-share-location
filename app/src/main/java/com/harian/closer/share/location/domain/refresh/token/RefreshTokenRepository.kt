package com.harian.closer.share.location.domain.refresh.token

import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.refresh.token.remote.dto.RefreshTokenResponse
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.refresh.token.entity.RefreshTokenEntity
import kotlinx.coroutines.flow.Flow

interface RefreshTokenRepository {
    suspend fun refresh(): Flow<BaseResult<RefreshTokenEntity, WrappedResponse<RefreshTokenResponse>>>
}
