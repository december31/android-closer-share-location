package com.harian.closer.share.location.data.user.remote.dto

import com.google.gson.annotations.SerializedName

data class UserResponse (
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("avatar") val avatar: String?,
    @SerializedName("gender") val gender: String?,
    @SerializedName("description") val description: String?,
)
