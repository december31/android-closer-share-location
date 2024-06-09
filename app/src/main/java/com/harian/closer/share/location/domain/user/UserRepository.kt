package com.harian.closer.share.location.domain.user

import com.harian.closer.share.location.data.common.utils.WrappedListResponse
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.post.remote.dto.PostDTO
import com.harian.closer.share.location.data.user.remote.dto.DeviceDTO
import com.harian.closer.share.location.data.user.remote.dto.FriendRequestDTO
import com.harian.closer.share.location.data.user.remote.dto.FriendsResponse
import com.harian.closer.share.location.data.user.remote.dto.UserDTO
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.post.entity.PostEntity
import com.harian.closer.share.location.domain.user.entity.DeviceEntity
import com.harian.closer.share.location.domain.user.entity.FriendRequestEntity
import com.harian.closer.share.location.domain.user.entity.FriendsEntity
import com.harian.closer.share.location.domain.user.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

interface UserRepository {
    suspend fun getUserInformation(): Flow<BaseResult<UserEntity, WrappedResponse<UserDTO>>>
    suspend fun getUserInformation(user: UserEntity): Flow<BaseResult<UserEntity, WrappedResponse<UserDTO>>>
    suspend fun getFriends(page: Int, limit: Int): Flow<BaseResult<FriendsEntity, WrappedResponse<FriendsResponse>>>
    suspend fun getFriends(user: UserEntity, page: Int, limit: Int): Flow<BaseResult<FriendsEntity, WrappedResponse<FriendsResponse>>>
    suspend fun getPosts(): Flow<BaseResult<List<PostEntity>, WrappedListResponse<PostDTO>>>
    suspend fun getPosts(user: UserEntity): Flow<BaseResult<List<PostEntity>, WrappedListResponse<PostDTO>>>
    suspend fun sendFriendRequest(user: UserEntity): Flow<BaseResult<UserEntity, WrappedResponse<UserDTO>>>
    suspend fun getFriendRequest(): Flow<BaseResult<List<FriendRequestEntity>, WrappedListResponse<FriendRequestDTO>>>
    suspend fun updateDeviceInformation(device: DeviceEntity): Flow<BaseResult<DeviceEntity, WrappedResponse<DeviceDTO>>>
    suspend fun acceptFriendRequest(user: UserEntity): Flow<BaseResult<UserEntity, WrappedResponse<UserDTO>>>
    suspend fun denyFriendRequest(user: UserEntity): Flow<BaseResult<UserEntity, WrappedResponse<UserDTO>>>
    suspend fun updateAvatar(part: MultipartBody.Part): Flow<BaseResult<UserEntity, WrappedResponse<UserDTO>>>
    suspend fun updateInformation(user: UserEntity): Flow<BaseResult<UserEntity, WrappedResponse<UserDTO>>>
    suspend fun logout()
}
