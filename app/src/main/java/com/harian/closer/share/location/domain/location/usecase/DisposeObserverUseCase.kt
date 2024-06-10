package com.harian.closer.share.location.domain.location.usecase

import com.harian.closer.share.location.domain.location.LocationRepository
import javax.inject.Inject

class DisposeObserverUseCase @Inject constructor(private val locationRepository: LocationRepository) {
    fun execute() {
        locationRepository.disposeObserver()
    }
}
