package com.harian.closer.share.location.domain.user.usecase

import android.util.Log
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
                userRepository.updateDeviceInformation(it)
                    .catch { exception ->
                        Log.e(
                            this@UpdateDeviceInformationUseCase.javaClass.simpleName,
                            exception.message.toString()
                        )
                    }
                    .collect()
            }
    }
}
