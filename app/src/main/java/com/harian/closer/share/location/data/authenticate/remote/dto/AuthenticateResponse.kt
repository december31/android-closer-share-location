package com.harian.closer.share.location.data.authenticate.remote.dto

import com.google.gson.annotations.SerializedName

data class AuthenticateResponse(
    @SerializedName("access_token") val accessToken: String?,
    @SerializedName("refresh_token") val refreshToken: String?,
)
