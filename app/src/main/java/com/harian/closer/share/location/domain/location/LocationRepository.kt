package com.harian.closer.share.location.domain.location

import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    suspend fun subscribeFriendsLocationUpdates(): Flow<String>

    suspend fun updateLocation(): Flow<String>
}
