package com.harian.closer.share.location.domain.user.usecase

import com.harian.closer.share.location.domain.user.UserRepository
import javax.inject.Inject

class ClearUserDataCacheUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend fun execute() {
        return userRepository.clearUserDataCache()
    }
}
