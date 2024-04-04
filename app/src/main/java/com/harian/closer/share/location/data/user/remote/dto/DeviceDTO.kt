package com.harian.closer.share.location.data.user.remote.dto

import com.google.gson.annotations.SerializedName
import com.harian.closer.share.location.domain.user.entity.DeviceEntity

data class DeviceDTO(
    @SerializedName("id") val id: String?,
    @SerializedName("model") val model: String?,
    @SerializedName("manufacturer") val manufacturer: String?,
    @SerializedName("brand") val brand: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("version-code-base") val versionCodeBase: Int?,
    @SerializedName("incremental") val incremental: String?,
    @SerializedName("sdk") val sdk: Int?,
    @SerializedName("board") val board: String?,
    @SerializedName("host") val host: String?,
    @SerializedName("fingerprint") val fingerprint: String?,
    @SerializedName("version-code") val versionCode: String?,
    @SerializedName("firebase-messaging-token") val firebaseMessagingToken: String?
) {
    companion object {
        fun fromDeviceEntity(deviceEntity: DeviceEntity): DeviceDTO {
            return DeviceDTO(
                id = deviceEntity.id,
                model = deviceEntity.model,
                manufacturer = deviceEntity.manufacturer,
                brand = deviceEntity.brand,
                type = deviceEntity.type,
                versionCodeBase = deviceEntity.versionCodeBase,
                incremental = deviceEntity.incremental,
                sdk = deviceEntity.sdk,
                board = deviceEntity.board,
                host = deviceEntity.host,
                fingerprint = deviceEntity.fingerprint,
                versionCode = deviceEntity.versionCode,
                firebaseMessagingToken = deviceEntity.firebaseMessagingToken
            )
        }
    }
}
