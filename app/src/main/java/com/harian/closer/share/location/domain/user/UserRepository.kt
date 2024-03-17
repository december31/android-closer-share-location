package com.harian.closer.share.location.domain.user

import com.harian.closer.share.location.data.common.utils.WrappedListResponse
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.post.remote.dto.PostResponse
import com.harian.closer.share.location.data.user.remote.dto.UserResponse
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.post.entity.PostEntity
import com.harian.closer.share.location.domain.user.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUserInformation(): Flow<BaseResult<UserEntity, WrappedResponse<UserResponse>>>
    suspend fun getFriends(): Flow<BaseResult<List<UserEntity>, WrappedListResponse<UserResponse>>>
    suspend fun getPosts(): Flow<BaseResult<List<PostEntity>, WrappedListResponse<PostResponse>>>
}
