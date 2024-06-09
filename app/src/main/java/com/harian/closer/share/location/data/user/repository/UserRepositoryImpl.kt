package com.harian.closer.share.location.data.user.repository

import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.harian.closer.share.location.data.common.utils.WrappedListResponse
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.post.remote.dto.PostDTO
import com.harian.closer.share.location.data.user.remote.api.UserApi
import com.harian.closer.share.location.data.user.remote.dto.DeviceDTO
import com.harian.closer.share.location.data.user.remote.dto.FriendRequestDTO
import com.harian.closer.share.location.data.user.remote.dto.FriendsResponse
import com.harian.closer.share.location.data.user.remote.dto.UserDTO
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.post.entity.PostEntity
import com.harian.closer.share.location.domain.user.UserRepository
import com.harian.closer.share.location.domain.user.entity.DeviceEntity
import com.harian.closer.share.location.domain.user.entity.FriendEntity
import com.harian.closer.share.location.domain.user.entity.FriendRequestEntity
import com.harian.closer.share.location.domain.user.entity.FriendsEntity
import com.harian.closer.share.location.domain.user.entity.UserEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import okhttp3.MultipartBody

class UserRepositoryImpl(private val userApi: UserApi) : UserRepository {

    private var userInformation: BaseResult.Success<UserEntity>? = null

    override suspend fun getUserInformation(): Flow<BaseResult<UserEntity, WrappedResponse<UserDTO>>> {
        return flow {
            if (userInformation == null) {
                val response = userApi.getUserInformation()
                if (response.isSuccessful && response.code() in 200 until 400) {
                    val body = response.body()
                    body?.data?.toEntity()?.let { userEntity ->
                        userInformation = BaseResult.Success(userEntity)
                        emit(userInformation!!)
                    }
                } else {
                    val type = object : TypeToken<WrappedResponse<UserDTO>>() {}.type
                    val error = Gson().fromJson<WrappedResponse<UserDTO>>(response.errorBody()?.charStream(), type)
                    emit(BaseResult.Error(error))
                }
            } else {
                delay(500)
                emit(userInformation!!)
            }
        }
    }

    override suspend fun getUserInformation(user: UserEntity): Flow<BaseResult<UserEntity, WrappedResponse<UserDTO>>> {
        return flow {
            val response = userApi.getUserInformation(user.id!!)
            if (response.isSuccessful && response.code() in 200 until 400) {
                val body = response.body()
                body?.data?.toEntity()?.let { userEntity ->
                    emit(BaseResult.Success(userEntity))
                }
            } else {
                val type = object : TypeToken<WrappedResponse<UserDTO>>() {}.type
                val error = Gson().fromJson<WrappedResponse<UserDTO>>(response.errorBody()?.charStream(), type)
                emit(BaseResult.Error(error))
            }
        }
    }

    override suspend fun getFriends(page: Int, limit: Int): Flow<BaseResult<FriendsEntity, WrappedResponse<FriendsResponse>>> {
        return flow {
            val response = userApi.getFriends(page, limit)
            if (response.isSuccessful && response.code() in 200 until 400) {
                val body = response.body()
                val friends = body?.data?.let {
                    FriendsEntity(
                        friends = body.data.friends.map { friendDTO ->
                            FriendEntity(
                                information = friendDTO.information.toEntity(),
                                since = friendDTO.since
                            )
                        },
                        count = body.data.count
                    )
                } ?: return@flow
                emit(BaseResult.Success(friends))
            } else {
                val type = object : TypeToken<WrappedResponse<FriendsResponse>>() {}.type
                val error = Gson().fromJson<WrappedResponse<FriendsResponse>>(response.errorBody()?.charStream(), type)
                emit(BaseResult.Error(error))
            }
        }
    }

    override suspend fun getFriends(user: UserEntity, page: Int, limit: Int): Flow<BaseResult<FriendsEntity, WrappedResponse<FriendsResponse>>> {
        return flow {
            val response = userApi.getFriends(user.id!!, page, limit)
            if (response.isSuccessful && response.code() in 200 until 400) {
                val body = response.body()
                val friends = body?.data?.let {
                    FriendsEntity(
                        friends = body.data.friends.map { friendDTO ->
                            FriendEntity(
                                information = friendDTO.information.toEntity(),
                                since = friendDTO.since
                            )
                        },
                        count = body.data.count
                    )
                } ?: return@flow
                emit(BaseResult.Success(friends))
            } else {
                val type = object : TypeToken<WrappedResponse<FriendsResponse>>() {}.type
                val error = Gson().fromJson<WrappedResponse<FriendsResponse>>(response.errorBody()?.charStream(), type)
                emit(BaseResult.Error(error))
            }
        }
    }

    override suspend fun getPosts(): Flow<BaseResult<List<PostEntity>, WrappedListResponse<PostDTO>>> {
        return flow {
            val response = userApi.getPosts()
            if (response.isSuccessful && response.code() in 200 until 400) {
                val body = response.body()
                val posts = body?.data?.map { it.toEntity() } ?: listOf()
                emit(BaseResult.Success(posts))
            } else {
                val type = object : TypeToken<WrappedListResponse<PostDTO>>() {}.type
                val error = Gson().fromJson<WrappedListResponse<PostDTO>>(response.errorBody()?.charStream(), type)
                emit(BaseResult.Error(error))
            }
        }
    }

    override suspend fun getPosts(user: UserEntity): Flow<BaseResult<List<PostEntity>, WrappedListResponse<PostDTO>>> {
        return flow {
            val response = userApi.getPosts(user.id!!)
            if (response.isSuccessful && response.code() in 200 until 400) {
                val body = response.body()
                val posts = body?.data?.map { it.toEntity() } ?: listOf()
                emit(BaseResult.Success(posts))
            } else {
                val type = object : TypeToken<WrappedListResponse<PostDTO>>() {}.type
                val error = Gson().fromJson<WrappedListResponse<PostDTO>>(response.errorBody()?.charStream(), type)
                emit(BaseResult.Error(error))
            }
        }
    }

    override suspend fun sendFriendRequest(user: UserEntity): Flow<BaseResult<UserEntity, WrappedResponse<UserDTO>>> {
        return flow {
            delay(2000)
            val response = userApi.sendFriendRequest(user.toDTO())
            if (response.isSuccessful && response.code() in 200 until 400) {
                val body = response.body()
                body?.data?.toEntity()?.let {
                    emit(BaseResult.Success(it))
                }
            } else {
                val type = object : TypeToken<WrappedResponse<UserDTO>>() {}.type
                val error = Gson().fromJson<WrappedResponse<UserDTO>>(response.errorBody()?.charStream(), type)
                emit(BaseResult.Error(error))
            }
        }
    }

    override suspend fun getFriendRequest(): Flow<BaseResult<List<FriendRequestEntity>, WrappedListResponse<FriendRequestDTO>>> {
        return flow {
            val response = userApi.getFriendRequest()
            if (response.isSuccessful && response.code() in 200 until 400) {
                val body = response.body()
                val friendRequests = body?.data?.map { it.toEntity() } ?: listOf()
                emit(BaseResult.Success(friendRequests))
            } else {
                val type = object : TypeToken<WrappedListResponse<FriendRequestDTO>>() {}.type
                val error = Gson().fromJson<WrappedListResponse<FriendRequestDTO>>(response.errorBody()?.charStream(), type)
                emit(BaseResult.Error(error))
            }
        }
    }

    override suspend fun updateDeviceInformation(device: DeviceEntity): Flow<BaseResult<DeviceEntity, WrappedResponse<DeviceDTO>>> {
        return flow {
            val firebaseMessagingToken = FirebaseMessaging.getInstance().token.await()
            val response = userApi.updateDeviceInformation(device.toDTO().apply {
                this.firebaseMessagingToken = firebaseMessagingToken
            })
            if (response.isSuccessful && response.code() in 200 until 400) {
                val body = response.body()
                body?.data?.toEntity()?.let {
                    emit(BaseResult.Success(it))
                }
            } else {
                val type = object : TypeToken<WrappedResponse<DeviceDTO>>() {}.type
                val error = Gson().fromJson<WrappedResponse<DeviceDTO>>(response.errorBody()?.charStream(), type)
                emit(BaseResult.Error(error))
            }
        }
    }

    override suspend fun acceptFriendRequest(user: UserEntity): Flow<BaseResult<UserEntity, WrappedResponse<UserDTO>>> {
        return flow {
            val response = userApi.acceptFriendRequest(user.toDTO())
            if (response.isSuccessful && response.code() in 200 until 400) {
                val body = response.body()
                body?.data?.toEntity()?.let { data ->
                    emit(BaseResult.Success(data))
                }
            } else {
                val type = object : TypeToken<WrappedResponse<UserDTO>>() {}.type
                val error = Gson().fromJson<WrappedResponse<UserDTO>>(response.errorBody()?.charStream(), type)
                emit(BaseResult.Error(error))
            }
        }
    }

    override suspend fun denyFriendRequest(user: UserEntity): Flow<BaseResult<UserEntity, WrappedResponse<UserDTO>>> {
        return flow {
            val response = userApi.denyFriendRequest(user.toDTO())
            if (response.isSuccessful && response.code() in 200 until 400) {
                val body = response.body()
                body?.data?.toEntity()?.let {
                    emit(BaseResult.Success(it))
                }
            } else {
                val type = object : TypeToken<WrappedResponse<UserDTO>>() {}.type
                val error = Gson().fromJson<WrappedResponse<UserDTO>>(response.errorBody()?.charStream(), type)
                emit(BaseResult.Error(error))
            }
        }
    }

    override suspend fun updateAvatar(part: MultipartBody.Part): Flow<BaseResult<UserEntity, WrappedResponse<UserDTO>>> {
        return flow {
            val response = userApi.updateAvatar(part)
            if (response.isSuccessful && response.code() in 200 until 400) {
                val body = response.body()
                body?.data?.toEntity()?.let {
                    emit(BaseResult.Success(it))
                }
            } else {
                val type = object : TypeToken<WrappedResponse<UserDTO>>() {}.type
                val error = Gson().fromJson<WrappedResponse<UserDTO>>(response.errorBody()?.charStream(), type)
                emit(BaseResult.Error(error))
            }
        }
    }

    override suspend fun updateInformation(user: UserEntity): Flow<BaseResult<UserEntity, WrappedResponse<UserDTO>>> {
        return flow {
            val response = userApi.updateInformation(user.toDTO())
            if (response.isSuccessful && response.code() in 200 until 400) {
                val body = response.body()
                body?.data?.toEntity()?.let {
                    emit(BaseResult.Success(it))
                }
            } else {
                val type = object : TypeToken<WrappedResponse<UserDTO>>() {}.type
                val error = Gson().fromJson<WrappedResponse<UserDTO>>(response.errorBody()?.charStream(), type)
                emit(BaseResult.Error(error))
            }
        }
    }

    override fun resetUserInformation() {
        this.userInformation = null
    }
}
