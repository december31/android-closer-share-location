package com.harian.closer.share.location.domain.user.entity

import com.harian.closer.share.location.data.user.remote.dto.DeviceDTO
import com.harian.closer.share.location.domain.common.base.BaseEntity

data class DeviceEntity(
    val id: String?,
    val model: String?,
    val manufacturer: String?,
    val brand: String?,
    val type: String?,
    val versionCodeBase: Int?,
    val incremental: String?,
    val sdk: Int?,
    val board: String?,
    val host: String?,
    val fingerprint: String?,
    val versionCode: String?,
    var firebaseMessagingToken: String?
) : BaseEntity<DeviceDTO> {
    override fun toDTO(): DeviceDTO {
        return DeviceDTO(
            id = id,
            model = model,
            manufacturer = manufacturer,
            brand = brand,
            type = type,
            versionCodeBase = versionCodeBase,
            incremental = incremental,
            sdk = sdk,
            board = board,
            host = host,
            fingerprint = fingerprint,
            versionCode = versionCode,
            firebaseMessagingToken = firebaseMessagingToken
        )
    }

}
