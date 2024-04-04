package com.harian.closer.share.location.domain.user.usecase

import com.harian.closer.share.location.domain.user.UserRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

class UpdateDeviceInformationUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val getDeviceInformationUseCase: GetDeviceInformationUseCase
) {
    suspend fun execute() {
        getDeviceInformationUseCase.execute()
            .catch {
                it.printStackTrace()
            }
            .collect {
                userRepository.updateDeviceInformation(it).collect()
            }
    }
}
