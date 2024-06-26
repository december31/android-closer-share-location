package com.harian.closer.share.location.domain.search

import com.harian.closer.share.location.data.common.utils.WrappedListResponse
import com.harian.closer.share.location.data.post.remote.dto.PostDTO
import com.harian.closer.share.location.data.user.remote.dto.UserDTO
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.post.entity.PostEntity
import com.harian.closer.share.location.domain.user.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    suspend fun searchUsers(query: String): Flow<BaseResult<List<UserEntity>, WrappedListResponse<UserDTO>>>
    suspend fun searchPosts(query: String): Flow<BaseResult<List<PostEntity>, WrappedListResponse<PostDTO>>>
}
