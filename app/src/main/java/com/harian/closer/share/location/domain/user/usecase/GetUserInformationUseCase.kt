package com.harian.closer.share.location.domain.user.usecase

import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.user.remote.dto.UserDTO
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.user.UserRepository
import com.harian.closer.share.location.domain.user.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserInformationUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend fun execute(user: UserEntity? = null): Flow<BaseResult<UserEntity, WrappedResponse<UserDTO>>> {
        return if (user != null) userRepository.getUserInformation(user) else userRepository.getUserInformation()
    }

    suspend fun execute(userId: Long): Flow<BaseResult<UserEntity, WrappedResponse<UserDTO>>> {
        return userRepository.getUserInformation(
            UserEntity(
                id = userId,
                name = null,
                email = null,
                avatar = null,
                gender = null,
                description = null,
                status = null,
                isFriend = null,
                latitude = null,
                longitude = null
            )
        )
    }
}
