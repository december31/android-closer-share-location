package com.harian.closer.share.location.domain.post.usecase

import com.harian.closer.share.location.data.common.utils.WrappedListResponse
import com.harian.closer.share.location.data.post.remote.dto.PostResponse
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.post.PostRepository
import com.harian.closer.share.location.domain.post.entity.PostEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPopularPostsUseCase @Inject constructor(private val postRepository: PostRepository) {
    suspend fun execute(
        page: Int? = null,
        pageSize: Int? = null
    ): Flow<BaseResult<List<PostEntity>, WrappedListResponse<PostResponse>>> {
        return postRepository.getPopularPosts(page, pageSize)
    }
}
