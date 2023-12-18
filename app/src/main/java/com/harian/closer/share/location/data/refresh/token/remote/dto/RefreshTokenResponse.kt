package com.harian.closer.share.location.data.refresh.token.remote.dto

import com.google.gson.annotations.SerializedName

data class RefreshTokenResponse (
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("avatar") val avatar: String?,
    @SerializedName("gender") val gender: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("access_token") val accessToken: String?,
    @SerializedName("refresh_token") val refreshToken: String?,
)
