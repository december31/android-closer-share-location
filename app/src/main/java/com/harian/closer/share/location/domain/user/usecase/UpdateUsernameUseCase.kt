package com.harian.closer.share.location.domain.user.usecase

import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.user.remote.dto.UserDTO
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.user.UserRepository
import com.harian.closer.share.location.domain.user.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateUsernameUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend fun execute(username: String): Flow<BaseResult<UserEntity, WrappedResponse<UserDTO>>> {
        return userRepository.updateUsername(username)
    }
}
