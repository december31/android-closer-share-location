package com.harian.closer.share.location.domain.post.usecase

import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.post.remote.dto.PostResponse
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.post.PostRepository
import com.harian.closer.share.location.domain.post.entity.PostEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LikePostUseCase @Inject constructor(private val postRepository: PostRepository) {
    suspend fun execute(postId: Int): Flow<BaseResult<PostEntity, WrappedResponse<PostResponse>>> {
        return postRepository.likePost(postId)
    }
}
