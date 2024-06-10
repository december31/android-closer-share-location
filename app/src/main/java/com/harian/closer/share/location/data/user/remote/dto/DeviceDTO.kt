package com.harian.closer.share.location.data.user.remote.dto

import com.google.gson.annotations.SerializedName
import com.harian.closer.share.location.data.common.base.BaseDTO
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
    @SerializedName("firebase-messaging-token") var firebaseMessagingToken: String?
) : BaseDTO<DeviceEntity> {
    override fun toEntity(): DeviceEntity {
        return DeviceEntity(
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
