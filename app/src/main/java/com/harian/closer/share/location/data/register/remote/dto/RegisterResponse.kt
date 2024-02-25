package com.harian.closer.share.location.data.register.remote.dto

import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    @SerializedName("access_token") val accessToken: String?,
    @SerializedName("refresh_token") val refreshToken: String?,
)
