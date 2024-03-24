package com.harian.closer.share.location.domain.user

import com.harian.closer.share.location.data.common.utils.WrappedListResponse
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.post.remote.dto.PostDTO
import com.harian.closer.share.location.data.user.remote.dto.UserDTO
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.post.entity.PostEntity
import com.harian.closer.share.location.domain.user.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUserInformation(): Flow<BaseResult<UserEntity, WrappedResponse<UserDTO>>>
    suspend fun getUserInformation(user: UserEntity): Flow<BaseResult<UserEntity, WrappedResponse<UserDTO>>>
    suspend fun getFriends(): Flow<BaseResult<List<UserEntity>, WrappedListResponse<UserDTO>>>
    suspend fun getFriends(user: UserEntity): Flow<BaseResult<List<UserEntity>, WrappedListResponse<UserDTO>>>
    suspend fun getPosts(): Flow<BaseResult<List<PostEntity>, WrappedListResponse<PostDTO>>>
    suspend fun getPosts(user: UserEntity): Flow<BaseResult<List<PostEntity>, WrappedListResponse<PostDTO>>>
    suspend fun sendFriendRequest(userDTO: UserEntity): Flow<BaseResult<UserEntity, WrappedResponse<UserDTO>>>
}
