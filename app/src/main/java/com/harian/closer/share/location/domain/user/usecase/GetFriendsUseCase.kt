package com.harian.closer.share.location.domain.user.usecase

import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.user.remote.dto.FriendsResponse
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.user.UserRepository
import com.harian.closer.share.location.domain.user.entity.FriendsEntity
import com.harian.closer.share.location.domain.user.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFriendsUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend fun execute(user: UserEntity?, page: Int = 0, limit: Int = 10): Flow<BaseResult<FriendsEntity, WrappedResponse<FriendsResponse>>> {
        return if (user != null) userRepository.getFriends(user, page, limit) else userRepository.getFriends(page, limit)
    }
}
