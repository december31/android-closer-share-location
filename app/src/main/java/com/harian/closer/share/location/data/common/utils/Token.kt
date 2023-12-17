package com.harian.closer.share.location.data.common.utils

import com.google.gson.annotations.SerializedName

data class Token(
    @SerializedName("access_token") val accessToken: String?,
    @SerializedName("refresh_token") val refreshToken: String?
)
