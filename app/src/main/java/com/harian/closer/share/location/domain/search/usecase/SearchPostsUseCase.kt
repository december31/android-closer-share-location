package com.harian.closer.share.location.domain.search.usecase

import com.harian.closer.share.location.data.common.utils.WrappedListResponse
import com.harian.closer.share.location.data.post.remote.dto.PostDTO
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.post.entity.PostEntity
import com.harian.closer.share.location.domain.search.SearchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchPostsUseCase @Inject constructor(private val searchRepository: SearchRepository) {
    suspend fun execute(query: String): Flow<BaseResult<List<PostEntity>, WrappedListResponse<PostDTO>>> {
        return searchRepository.searchPosts(query)
    }
}
