package com.harian.closer.share.location.domain.user.usecase

import com.harian.closer.share.location.data.common.utils.WrappedListResponse
import com.harian.closer.share.location.data.post.remote.dto.PostDTO
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.post.entity.PostEntity
import com.harian.closer.share.location.domain.user.UserRepository
import com.harian.closer.share.location.domain.user.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPostsUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend fun execute(user: UserEntity?): Flow<BaseResult<List<PostEntity>, WrappedListResponse<PostDTO>>> {
        return if (user != null) userRepository.getPosts(user) else userRepository.getPosts()
    }
}
