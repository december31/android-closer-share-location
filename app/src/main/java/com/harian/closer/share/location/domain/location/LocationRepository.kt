package com.harian.closer.share.location.domain.location

import com.harian.closer.share.location.data.location.dto.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    suspend fun subscribeFriendsLocationUpdates(): Flow<String>

    suspend fun updateLocation(location: Location): Flow<String>
    fun disposeObserver()
}
