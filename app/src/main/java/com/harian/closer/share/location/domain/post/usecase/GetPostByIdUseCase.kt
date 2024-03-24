package com.harian.closer.share.location.domain.post.usecase

import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.post.remote.dto.PostDTO
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.post.PostRepository
import com.harian.closer.share.location.domain.post.entity.PostEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPostByIdUseCase @Inject constructor(private val postRepository: PostRepository) {
    suspend fun execute(id: Int): Flow<BaseResult<PostEntity, WrappedResponse<PostDTO>>> {
        return postRepository.getPostById(id)
    }
}
