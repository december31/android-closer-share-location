package com.harian.closer.share.location.domain.location

import com.harian.closer.share.location.data.location.dto.Location
import com.harian.closer.share.location.domain.user.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    suspend fun subscribeFriendsLocationUpdates(userEntity: UserEntity): Flow<UserEntity?>

    suspend fun updateLocation(location: Location): Flow<String>
    fun disposeObserver()
}
