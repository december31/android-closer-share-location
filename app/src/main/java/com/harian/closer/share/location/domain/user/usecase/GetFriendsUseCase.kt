package com.harian.closer.share.location.domain.user.usecase

import com.harian.closer.share.location.data.common.utils.WrappedListResponse
import com.harian.closer.share.location.data.user.remote.dto.UserResponse
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.user.UserRepository
import com.harian.closer.share.location.domain.user.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFriendsUseCase @Inject constructor(private val userRepository: UserRepository){
    suspend fun execute(): Flow<BaseResult<List<UserEntity>, WrappedListResponse<UserResponse>>> {
        return userRepository.getFriends()
    }
}
