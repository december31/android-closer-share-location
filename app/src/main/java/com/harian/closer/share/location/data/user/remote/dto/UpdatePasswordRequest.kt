package com.harian.closer.share.location.data.user.remote.dto

data class UpdatePasswordRequest(
    val currentPassword: String?,
    val newPassword: String?,
)
