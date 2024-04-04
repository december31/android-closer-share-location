package com.harian.closer.share.location.domain.user.usecase

import android.os.Build
import com.harian.closer.share.location.domain.user.entity.DeviceEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetDeviceInformationUseCase @Inject constructor() {
    suspend fun execute(): Flow<DeviceEntity> {
        return flow {
            emit(
                DeviceEntity(
                    id = getDeviceId(),
                    model = Build.MODEL,
                    manufacturer = Build.MANUFACTURER,
                    brand = Build.BRAND,
                    type = Build.TYPE,
                    versionCodeBase = Build.VERSION_CODES.BASE,
                    incremental = Build.VERSION.INCREMENTAL,
                    sdk = Build.VERSION.SDK_INT,
                    board = Build.BOARD,
                    host = Build.HOST,
                    fingerprint = Build.FINGERPRINT,
                    versionCode = Build.VERSION.RELEASE,
                    null
                )
            )
        }
    }

    private fun getDeviceId(): String {
        return "35" +
                Build.BOARD.length +
                Build.BRAND.length +
                Build.DEVICE.length +
                Build.DISPLAY.length +
                Build.HOST.length +
                Build.ID.length +
                Build.MANUFACTURER.length +
                Build.MODEL.length +
                Build.PRODUCT.length +
                Build.TAGS.length +
                Build.TYPE.length +
                Build.USER.length
    }
}
