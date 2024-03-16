package com.harian.closer.share.location.domain.location.usecase

import com.harian.closer.share.location.data.location.dto.Location
import com.harian.closer.share.location.domain.location.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateLocationUseCase @Inject constructor(private val locationRepository: LocationRepository) {
    suspend fun execute(location: Location): Flow<String> {
        return locationRepository.updateLocation(location)
    }
}
