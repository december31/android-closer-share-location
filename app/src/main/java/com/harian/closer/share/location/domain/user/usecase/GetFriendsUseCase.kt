package com.harian.closer.share.location.domain.user.usecase

import com.harian.closer.share.location.data.common.utils.WrappedListResponse
import com.harian.closer.share.location.data.user.remote.dto.UserDTO
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.user.UserRepository
import com.harian.closer.share.location.domain.user.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFriendsUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend fun execute(user: UserEntity?): Flow<BaseResult<List<UserEntity>, WrappedListResponse<UserDTO>>> {
        return if (user != null) userRepository.getFriends(user) else userRepository.getFriends()
    }
}
