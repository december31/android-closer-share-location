package com.harian.closer.share.location.data.login.remote.dto

import com.google.gson.annotations.SerializedName
import com.harian.closer.share.location.data.common.utils.Token

data class LoginResponse(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("avatar") val avatar: String?,
    @SerializedName("gender") val gender: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("access_token") val accessToken: String?,
    @SerializedName("refresh_token") val refreshToken: String?,
)
