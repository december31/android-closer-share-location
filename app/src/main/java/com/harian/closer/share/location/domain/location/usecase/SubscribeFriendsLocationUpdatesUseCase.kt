package com.harian.closer.share.location.domain.location.usecase

import com.harian.closer.share.location.domain.location.LocationRepository
import com.harian.closer.share.location.domain.user.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SubscribeFriendsLocationUpdatesUseCase @Inject constructor(private val locationRepository: LocationRepository) {
    suspend fun execute(userEntity: UserEntity): Flow<UserEntity?> {
        return locationRepository.subscribeFriendsLocationUpdates(userEntity)
    }
}
