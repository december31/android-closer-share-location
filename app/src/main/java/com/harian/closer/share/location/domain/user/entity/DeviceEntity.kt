package com.harian.closer.share.location.domain.user.entity

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
)
